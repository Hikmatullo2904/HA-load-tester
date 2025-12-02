package uz.hikmatullo.loadtesting.util;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.extern.slf4j.Slf4j;
import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.exceptions.ExtractionRuleException;
import uz.hikmatullo.loadtesting.model.entity.ExtractionRule;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Highly optimized extraction utility:
 * - Reuses a shared JsonPath ParseContext (Jackson-backed)
 * - Caches compiled JsonPath expressions to avoid re-parsing path strings
 * - Parses JSON once per invocation (unavoidable when extracting values)
 * <p>
 * Notes:
 * - This class intentionally prefers correctness and throughput.
 * - If you have a small set of rules per test, consider pre-compiling them
 *   outside the hot path and storing compiled JsonPath in the rule object.
 */
@Slf4j
public class ExtractionRuleUtil {

    // shared, thread-safe JsonPath context using Jackson provider.
    private static final ParseContext JSON_PATH_CTX = JsonPath.using(
            Configuration.builder()
                    .jsonProvider(new JacksonJsonProvider())
                    .mappingProvider(new JacksonMappingProvider())
                    .build()
    );

    // Cache compiled JsonPath expressions to avoid recompiling the same path repeatedly.
    // Key = normalized path string (e.g. "$.data.token")
    private static final ConcurrentMap<String, com.jayway.jsonpath.JsonPath> COMPILED_PATH_CACHE =
            new ConcurrentHashMap<>();

    /**
     * Apply a single extraction rule to responseBody and save result into ctx.variables.
     * <p>
     * Throws:
     *  - ExtractionRuleException for user/client errors (bad JSON path, not JSON, missing field)
     *  - RuntimeException for unexpected internal errors
     */
    public static void applyRule(ExtractionRule rule, String responseBody, ExecutionContext ctx) {
        Objects.requireNonNull(rule, "ExtractionRule must not be null");
        if (rule.getSaveAs() == null) throw new IllegalArgumentException("ExtractionRule.saveAs is null");

        String path = rule.getJsonPath();
        if (path == null) throw new IllegalArgumentException("ExtractionRule.jsonPath is null");

        try {
            Object extracted;

            // "$" or "$." → raw string response (no parsing)
            if ("$".equals(path) || "$.".equals(path)) {
                extracted = responseBody;
            } else {
                String normalizedPath = normalizePath(path);

                // Get compiled JsonPath from cache (or compile once)
                com.jayway.jsonpath.JsonPath compiled = COMPILED_PATH_CACHE.computeIfAbsent(
                        normalizedPath,
                        JsonPath::compile
                );

                // Parse the JSON once and then evaluate the compiled path on the parsed document
                DocumentContext doc;
                try {
                    doc = JSON_PATH_CTX.parse(responseBody);
                } catch (com.jayway.jsonpath.InvalidJsonException jsonError) {
                    throw new ExtractionRuleException(
                            "Response is not valid JSON but JSONPath was provided: " + path,
                            jsonError
                    );
                }

                try {
                    extracted = doc.read(compiled); // evaluate compiled path on parsed doc
                } catch (PathNotFoundException ex) {
                    throw new ExtractionRuleException(
                            "JSONPath not found: '" + normalizedPath + "'",
                            ex
                    );
                } catch (InvalidPathException ex) {
                    // Shouldn't usually happen because we compiled earlier, but catch defensively
                    throw new ExtractionRuleException(
                            "Invalid JSONPath syntax '" + normalizedPath + "': " + ex.getMessage(),
                            ex
                    );
                } catch (Exception other) {
                    throw new ExtractionRuleException(
                            "Failed to evaluate JSONPath '" + normalizedPath + "': " + other.getMessage(),
                            other
                    );
                }
            }

            // Save into execution context
            ctx.getVariables().put(rule.getSaveAs(), extracted);

            if (log.isDebugEnabled()) {
                log.debug("Extraction successful: {} = {}", rule.getSaveAs(), extracted);
            }

        } catch (ExtractionRuleException e) {
            // client-visible error — bubble up
            log.debug("ExtractionRuleException: {}", e.getMessage());
            throw e;
        } catch (Exception unexpected) {
            // internal bug — wrap and bubble
            log.error("Internal extraction error: {}", unexpected.getMessage(), unexpected);
            throw new RuntimeException("Internal extraction failure", unexpected);
        }
    }

    /** Normalize JSONPath: id -> $.id ; .id -> $.id ; $.id -> $.id */
    private static String normalizePath(String path) {
        if (path.startsWith("$")) return path;
        if (path.startsWith(".")) return "$" + path;
        return "$." + path;
    }
}
