package uz.hikmatullo.loadtesting.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.exceptions.ExtractionRuleException;
import uz.hikmatullo.loadtesting.model.entity.ExtractionRule;

@Slf4j
public class ExtractionRuleUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Apply a single extraction rule.
     * Throws:
     *  - ExtractionRuleException → user/client error (bad JSON path, not JSON, missing field)
     *  - RuntimeException → internal rule processing problem
     */
    public static void applyRule(ExtractionRule rule, String responseBody, ExecutionContext ctx) {
        if (rule == null || rule.getSaveAs() == null) {
            throw new IllegalArgumentException("Rule or saveAs is null");
        }

        String path = rule.getJsonPath();
        if (path == null) {
            throw new IllegalArgumentException("JsonPath is null");
        }

        try {

            Object extracted;

            // "$" → raw string response
            if (path.equals("$") || path.equals("$.")) {
                extracted = responseBody;
            } else {
                JsonNode jsonNode = tryParseJson(responseBody);
                if (jsonNode == null) {
                    throw new ExtractionRuleException(
                            "Response is not valid JSON but JSONPath was provided: " + path
                    );
                }

                String normalizedPath = normalizePath(path);

                try {
                    extracted = JsonPath.read(jsonNode.toString(), normalizedPath);
                } catch (Exception jsonPathError) {
                    throw new ExtractionRuleException(
                            "Invalid JSONPath '" + normalizedPath + "' in extraction rule: " + jsonPathError.getMessage(),
                            jsonPathError
                    );
                }
            }

            // Save into context
            ctx.getVariables().put(rule.getSaveAs(), extracted);

            log.debug("Extracted: {} = {}", rule.getSaveAs(), extracted);

        } catch (ExtractionRuleException e) {
            // Client error – stop step execution
            log.error("ExtractionRuleException: {}", e.getMessage());
            throw e;

        } catch (Exception internal) {
            // Internal bug – this is our mistake
            log.error("Internal extraction processing error: {}", internal.getMessage());
            throw new RuntimeException("Internal extraction processing failure", internal);
        }
    }

    /** Parse JSON or return null */
    private static JsonNode tryParseJson(String body) {
        try {
            return MAPPER.readTree(body);
        } catch (Exception ignored) {
            return null;
        }
    }

    /** Normalize paths: id → $.id / .id → $.id / $.id → $.id */
    private static String normalizePath(String path) {
        if (path.startsWith("$")) return path;
        if (path.startsWith(".")) return "$" + path;
        return "$." + path;
    }
}
