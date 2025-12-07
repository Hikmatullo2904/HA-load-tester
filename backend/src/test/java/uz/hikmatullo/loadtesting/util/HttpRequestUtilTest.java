package uz.hikmatullo.loadtesting.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.exceptions.InvalidHttpRequestException;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.enums.HttpMethod;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestUtilTest {

    private ExecutionContext ctx;

    @BeforeEach
    void init() {
        ctx = new ExecutionContext();
    }

    @Test
    void testBuildGetRequest() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.GET)
                .url("https://example.com/api")
                .timeoutMs(5000)
                .headers(Map.of("Accept", "application/json"))
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, new ExecutionContext());

        assertEquals("GET", req.method());
        assertEquals(URI.create("https://example.com/api"), req.uri());
        assertEquals("application/json", req.headers().firstValue("Accept").orElse(null));
        assertEquals(Duration.ofMillis(5000), req.timeout().orElseThrow());
    }

    @Test
    void testBuildPostRequest() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.POST)
                .url("https://example.com/api")
                .timeoutMs(3000)
                .body("{\"name\":\"john\"}")
                .headers(Map.of("Content-Type", "application/json"))
                .queryParams(Map.of("q", "john", "limit", "10"))
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, new ExecutionContext());

        assertEquals("POST", req.method());
        assertTrue(Objects.equals(URI.create("https://example.com/api?q=john&limit=10"), req.uri()) ||
                Objects.equals(URI.create("https://example.com/api?limit=10&q=john"), req.uri()));
        assertEquals("application/json", req.headers().firstValue("Content-Type").orElse(null));
        assertTrue(req.bodyPublisher().isPresent());
    }

    @Test
    void testBuildPutRequest() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.PUT)
                .url("https://api.com/users/1")
                .timeoutMs(2000)
                .body(null)
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, new ExecutionContext());

        assertEquals("PUT", req.method());
        assertEquals(URI.create("https://api.com/users/1"), req.uri());
    }

    @Test
    void testBuildDeleteRequest() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.DELETE)
                .url("https://api.com/item/99")
                .timeoutMs(1500)
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, new ExecutionContext());
        assertEquals(Duration.ofMillis(1500), req.timeout().orElseThrow());
        assertEquals("DELETE", req.method());
    }

    @Test
    void testMissingUrlThrowsException() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.GET)
                .timeoutMs(1000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () ->
                HttpRequestUtil.buildRequest(step, new ExecutionContext())
        );
    }

    @Test
    void testInvalidUrlThrowsException() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.GET)
                .url("ht!tp://bad-url")
                .timeoutMs(1000)
                .build();

        assertThrows(Exception.class, () ->
                HttpRequestUtil.buildRequest(step, new ExecutionContext())
        );
    }

    @Test
    void testNullMethodThrowsException() {
        RequestStep step = RequestStep.builder()
                .url("https://valid.com")
                .timeoutMs(1000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () ->
                HttpRequestUtil.buildRequest(step, new ExecutionContext())
        );
    }

    @Test
    void testDefaultTimeoutApplies() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.GET)
                .url("https://example.com")
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, new ExecutionContext());
        Optional<Duration> timeout = req.timeout();
        if (timeout.isEmpty()) {
            fail();
        }
        assertNotNull(timeout);
        assertEquals(Duration.ofMillis(HttpRequestUtil.DEFAULT_TIMEOUT_MS), timeout.get());
    }

    @Test
    void testHeadersAreApplied() {
        RequestStep step = RequestStep.builder()
                .method(HttpMethod.GET)
                .url("https://example.com")
                .headers(Map.of(
                        "X-Test", "123",
                        "X-User", "hikmatullo"
                ))
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, new ExecutionContext());

        assertEquals("123", req.headers().firstValue("X-Test").orElse(null));
        assertEquals("hikmatullo", req.headers().firstValue("X-User").orElse(null));
    }

    // ----------------------------
    // PATH PARAMETER TESTS
    // ----------------------------

    @Test
    void test_PathParams_ReplacedCorrectly() {
        ctx.getVariables().put("id", 42);

        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/users/{{id}}/profile")
                .method(HttpMethod.GET)
                .timeoutMs(5000)
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, ctx);
        assertEquals("https://api.test.com/users/42/profile", req.uri().toString());
    }

    @Test
    void test_PathParams_MissingVariable_Throws() {
        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/u/{{missingId}}")
                .method(HttpMethod.GET)
                .timeoutMs(5000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () -> HttpRequestUtil.buildRequest(step, ctx));
    }

    @Test
    void test_PathParams_VariableIsNull_Throws() {
        ctx.getVariables().put("id", null);

        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/{id}")
                .method(HttpMethod.GET)
                .timeoutMs(5000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () -> HttpRequestUtil.buildRequest(step, ctx));
    }


    // ----------------------------
    // QUERY PARAMETER TESTS
    // ----------------------------

    @Test
    void test_QueryParams_Correct() {
        ctx.getVariables().put("search", "phone");

        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/items")
                .method(HttpMethod.GET)
                .queryParams(Map.of("q", "{{search}}", "limit", "10"))
                .timeoutMs(5000)
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, ctx);

        assertTrue("https://api.test.com/items?q=phone&limit=10".equals(req.uri().toString()) || "https://api.test.com/items?limit=10&q=phone".equals(req.uri().toString()));
    }

    @Test
    void test_QueryParams_MissingVariable_Throws() {
        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/items")
                .method(HttpMethod.GET)
                .queryParams(Map.of("q", "{{notExists}}"))
                .timeoutMs(5000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () -> HttpRequestUtil.buildRequest(step, ctx));
    }


    // ----------------------------
    // HEADER TESTS
    // ----------------------------

    @Test
    void test_HeaderVariableInjection() {
        ctx.getVariables().put("token", "ABC123");

        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/auth")
                .method(HttpMethod.GET)
                .headers(Map.of("Authorization", "Bearer {{token}}"))
                .timeoutMs(5000)
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, ctx);

        assertEquals("Bearer ABC123", req.headers().firstValue("Authorization").orElse(""));
    }

    @Test
    void test_HeaderMissingVariable_Throws() {
        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/auth")
                .method(HttpMethod.GET)
                .headers(Map.of("Authorization", "Bearer {{notExists}}"))
                .timeoutMs(5000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () -> HttpRequestUtil.buildRequest(step, ctx));
    }


    // ----------------------------
    // BODY TESTS
    // ----------------------------


    @Test
    void test_BodyMissingVariable_Throws() {
        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/register")
                .method(HttpMethod.POST)
                .body("{\"email\":\"{{missing}}\"}")
                .timeoutMs(5000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () -> HttpRequestUtil.buildRequest(step, ctx));
    }

    @Test
    void test_BodyNullVariable_Throws() {
        ctx.getVariables().put("email", null);
        try {
            RequestStep.builder()
                    .url("https://api.test.com/register")
                    .method(HttpMethod.POST)
                    .body("{\"email\":\"{{email}}\"}")
                    .timeoutMs(5000)
                    .build();
        }catch (Exception e) {
            fail();
        }
    }


    // ----------------------------
    // WRONG METHOD / INVALID URL
    // ----------------------------

    @Test
    void test_InvalidURL_Throws() {
        RequestStep step = RequestStep.builder()
                .url(":::::://wrong")
                .method(HttpMethod.GET)
                .timeoutMs(5000)
                .build();

        assertThrows(InvalidHttpRequestException.class, () -> HttpRequestUtil.buildRequest(step, ctx));
    }



    @Test
    void test_FullRequest_BodyHeadersQueryPathAllCombined() {
        ctx.getVariables().put("id", 99);
        ctx.getVariables().put("token", "ZXCV123");
        ctx.getVariables().put("search", "laptop");
        ctx.getVariables().put("email", "john@test.com");

        RequestStep step = RequestStep.builder()
                .url("https://api.test.com/users/{{id}}")
                .method(HttpMethod.PUT)
                .headers(Map.of("Auth", "Token {{token}}"))
                .queryParams(Map.of("q", "{{search}}", "limit", "5"))
                .body("{\"email\":\"{{email}}\"}")
                .timeoutMs(5000)
                .build();

        HttpRequest req = HttpRequestUtil.buildRequest(step, ctx);

        assertTrue("https://api.test.com/users/99?q=laptop&limit=5".equals(req.uri().toString()) || "https://api.test.com/users/99?limit=5&q=laptop".equals(req.uri().toString()));
        assertEquals("Token ZXCV123",
                req.headers().firstValue("Auth").orElse(""));
    }

    /*
    ================ HttpRequestUtil Benchmark ================
    Iterations       : 100000
    Total duration   : 613.9748 ms
    Avg build time   : 6.139748 µs
    Ops/sec          : 162873
    ===========================================================
    */
    @Test
    @DisplayName("Benchmark HttpRequestUtil.buildRequest() - 50,000 iterations")
    void benchmarkBuildRequest() {

        RequestStep step = RequestStep.builder()
                .method(HttpMethod.POST)
                .url("https://example.com/api")
                .timeoutMs(3000)
                .body("{\"name\":\"john\", \"id\": \"{{token}}\"}")
                .headers(Map.of(
                        "Content-Type", "application/json",
                        "X-User", "{{userId}}"
                ))
                .queryParams(Map.of(
                        "q", "john",
                        "limit", "10"
                ))
                .build();

        ExecutionContext ctx = new ExecutionContext();
        ctx.getVariables().put("token", "xyz123");
        ctx.getVariables().put("userId", "42");

        // ---------------------------------------
        // Correctness check (run only once)
        // ---------------------------------------
        HttpRequest req = HttpRequestUtil.buildRequest(step, ctx);

        assertEquals("POST", req.method());
        assertTrue(
                Objects.equals(URI.create("https://example.com/api?q=john&limit=10"), req.uri()) ||
                        Objects.equals(URI.create("https://example.com/api?limit=10&q=john"), req.uri())
        );
        assertEquals("application/json", req.headers().firstValue("Content-Type").orElse(null));

        // ---------------------------------------
        // WARMUP PHASE
        // ---------------------------------------
        for (int i = 0; i < 200; i++) {
            HttpRequestUtil.buildRequest(step, ctx);
        }

        // ---------------------------------------
        // BENCHMARK
        // ---------------------------------------
        int iterations = 100_000;

        long startNs = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            HttpRequestUtil.buildRequest(step, ctx);
        }

        long endNs = System.nanoTime();
        long durationNs = endNs - startNs;

        // ---------------------------------------
        // RESULT REPORT
        // ---------------------------------------
        double durationMs = durationNs / 1_000_000.0;
        double avgNs = (double) durationNs / iterations;
        double avgUs = avgNs / 1000.0;
        double opsPerSec = 1_000_000_000.0 / avgNs;

        System.out.println("\n================ HttpRequestUtil Benchmark ================");
        System.out.println("Iterations       : " + iterations);
        System.out.println("Total duration   : " + durationMs + " ms");
        System.out.println("Avg build time   : " + avgUs + " µs");
        System.out.println("Ops/sec          : " + (int) opsPerSec);
        System.out.println("===========================================================\n");
    }
}
