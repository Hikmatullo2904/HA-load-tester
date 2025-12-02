package uz.hikmatullo.loadtesting.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.exceptions.ExtractionRuleException;
import uz.hikmatullo.loadtesting.model.entity.ExtractionRule;

import static org.junit.jupiter.api.Assertions.*;

public class ExtractionRuleUtilTest {

    private ExecutionContext ctx;

    @BeforeEach
    void setup() {
        ctx = new ExecutionContext();
    }

    // ------------------------------------------------------
    // SUCCESS CASES
    // ------------------------------------------------------

    @Test
    @DisplayName("Should extract raw body when jsonPath = \"$\"")
    void testRawExtraction() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$")
                .saveAs("raw")
                .build();

        String body = "hello world";

        ExtractionRuleUtil.applyRule(rule, body, ctx);

        assertEquals("hello world", ctx.getVariables().get("raw"));
    }

    @Test
    @DisplayName("Should extract simple JSON field: $.id")
    void testJsonFieldExtraction() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.id")
                .saveAs("id")
                .build();

        String body = "{\"id\": 123, \"name\": \"abc\"}";

        ExtractionRuleUtil.applyRule(rule, body, ctx);

        assertEquals(123, ctx.getVariables().get("id"));
    }

    @Test
    @DisplayName("Should normalize missing '$.' prefix")
    void testPathNormalization() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("id")
                .saveAs("id2")
                .build();

        String body = "{\"id\": 99}";

        ExtractionRuleUtil.applyRule(rule, body, ctx);

        assertEquals(99, ctx.getVariables().get("id2"));
    }

    @Test
    @DisplayName("Should extract nested JSON using array index")
    void testJsonArrayExtraction() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.items[0].name")
                .saveAs("name")
                .build();

        String body = "{\"items\": [{\"name\": \"item1\"}, {\"name\": \"item2\"}]}";

        ExtractionRuleUtil.applyRule(rule, body, ctx);

        assertEquals("item1", ctx.getVariables().get("name"));
    }

    @Test
    @DisplayName("Should extract null value")
    void test_whenValueIsNull() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.id")
                .saveAs("id")
                .build();

        String body = "{\"id\": null}";

        ExtractionRuleUtil.applyRule(rule, body, ctx);
        if (!ctx.getVariables().containsKey("id")) {
            fail("Variable 'id' not found in context");
        }

        assertNull(ctx.getVariables().get("id"));
    }

    // ------------------------------------------------------
    // FAILURE CASES (THROW EXTRACTION RULE EXCEPTION)
    // ------------------------------------------------------

    @Test
    @DisplayName("Should throw ExtractionRuleException when JSONPath used but body is not JSON")
    void testJsonPathOnNonJsonBody() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.id")
                .saveAs("bad")
                .build();

        String body = "this is not json";

        assertThrows(ExtractionRuleException.class,
                () -> ExtractionRuleUtil.applyRule(rule, body, ctx));
    }

    @Test
    @DisplayName("Should throw ExtractionRuleException for invalid JSONPath")
    void testInvalidJsonPath() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.items[0].wrong..path")
                .saveAs("bad")
                .build();

        String body = "{\"items\": [{\"id\": 1}]}";

        assertThrows(ExtractionRuleException.class,
                () -> ExtractionRuleUtil.applyRule(rule, body, ctx));
    }

    // ------------------------------------------------------
    // BAD PROGRAMMER RULES → RuntimeException
    // ------------------------------------------------------

    @Test
    @DisplayName("Should throw RuntimeErrorException when saveAs is null")
    void testInvalidRuleNoSaveAs() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.id")
                .saveAs(null)
                .build();

        String body = "{\"id\": 5}";

        assertThrows(IllegalArgumentException.class,
                () -> ExtractionRuleUtil.applyRule(rule, body, ctx));
    }

    @Test
    @DisplayName("Should throw RuntimeErrorException when jsonPath is null")
    void testInvalidRuleJsonPathNull() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath(null)
                .saveAs("v")
                .build();

        String body = "{\"id\": 5}";

        assertThrows(IllegalArgumentException.class,
                () -> ExtractionRuleUtil.applyRule(rule, body, ctx));
    }

    @Test
    @DisplayName("Should throw ExtractionRuleException when extraction produces null")
    void testExtractionProducesNull() {
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.unknownField")
                .saveAs("missing")
                .build();

        String body = "{\"id\": 1}";

        assertThrows(ExtractionRuleException.class,
                () -> ExtractionRuleUtil.applyRule(rule, body, ctx));
    }

    /*
    ============ ExtractionRuleUtil Performance ============
    Iterations      : 150000
    Total time      : 982.9151 ms
    Avg per extract : 6.552767333333334 µs
    Ops/sec         : 152607
    ========================================================
    */
    @Test
    @DisplayName("Performance test for ExtractionRuleUtil (5000 iterations)")
    void extractionPerformanceTest() {

        // Rule to benchmark
        ExtractionRule rule = ExtractionRule.builder()
                .jsonPath("$.items[0].name")
                .saveAs("name")
                .build();

        // Sample JSON body
        String body = """
                {
                  "items": [
                    {"name": "item1"},
                    {"name": "item2"}
                  ]
                }
                """;

        ExecutionContext ctx = new ExecutionContext();

        // ---------------------------
        // WARMUP PHASE (important!)
        // ---------------------------
        for (int i = 0; i < 500; i++) {
            ExtractionRuleUtil.applyRule(rule, body, ctx);
        }

        // ---------------------------
        // MEASUREMENT
        // ---------------------------
        int iterations = 150000;

        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            ExtractionRuleUtil.applyRule(rule, body, ctx);
        }

        long end = System.nanoTime();

        // ---------------------------
        // RESULTS
        // ---------------------------
        long durationNs = end - start;
        double durationMs = durationNs / 1_000_000.0;
        double avgNs = durationNs / (double) iterations;
        double avgUs = avgNs / 1000.0;
        double opsPerSec = (1_000_000_000.0 / avgNs);

        System.out.println("============ ExtractionRuleUtil Performance ============");
        System.out.println("Iterations      : " + iterations);
        System.out.println("Total time      : " + durationMs + " ms");
        System.out.println("Avg per extract : " + avgUs + " µs");
        System.out.println("Ops/sec         : " + ((int) opsPerSec));
        System.out.println("========================================================");
    }
}
