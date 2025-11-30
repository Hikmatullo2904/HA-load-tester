package uz.hikmatullo.loadtesting.util;

import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.exceptions.InvalidHttpRequestException;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.enums.HttpMethod;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds java.net.http.HttpRequest based on RequestStep + ExecutionContext.
 * - Supports {{var}} substitution in URL, headers, query params, and body
 * - Correctly encodes query params
 * - Supports GET, POST, PUT, PATCH, DELETE
 * - Throws InvalidRequestException on invalid configuration
 */
public final class HttpRequestUtil {

    private HttpRequestUtil() {}

    public static final long DEFAULT_TIMEOUT_MS = 5000;

    private static final Pattern PLACEHOLDER =
            Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_.-]+)\\s*}}");

    /**
     * Build HttpRequest from RequestStep.
     */
    public static HttpRequest buildRequest(RequestStep step, ExecutionContext ctx)
            throws InvalidHttpRequestException {

        if (step == null) {
            throw new InvalidHttpRequestException("RequestStep is null");
        }

        // ---------- URL + query parameters ----------
        String substitutedUrl = substitute(step.getUrl(), ctx);
        if (substitutedUrl == null || substitutedUrl.isBlank()) {
            throw new InvalidHttpRequestException("RequestStep.url is empty after placeholder substitution");
        }

        String finalUrl = buildUrlWithQueryParams(substitutedUrl, step.getQueryParams(), ctx);

        URI uri;
        try {
            uri = URI.create(finalUrl);
        } catch (Exception e) {
            throw new InvalidHttpRequestException("Invalid URL: " + finalUrl);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);

        // ---------- Timeout ----------
        if (step.getTimeoutMs() != null && step.getTimeoutMs() > 0) {
            builder.timeout(Duration.ofMillis(step.getTimeoutMs()));
        }else {
            builder.timeout(Duration.ofMillis(DEFAULT_TIMEOUT_MS));
        }

        // ---------- Headers ----------
        if (step.getHeaders() != null) {
            for (var entry : step.getHeaders().entrySet()) {
                String k = entry.getKey();
                String v = substitute(entry.getValue(), ctx);
                if (k != null && v != null) {
                    builder.header(k, v);
                }
            }
        }

        // ---------- Body processing ----------
        String substitutedBody = step.getBody() == null
                ? null
                : substitute(step.getBody(), ctx);

        if (step.getMethod() == null) {
            throw new InvalidHttpRequestException("RequestStep.method is null");
        }
        HttpMethod method = step.getMethod();

        switch (method) {
            case GET -> builder.GET();
            case DELETE -> builder.DELETE();
            case POST -> builder.POST(bodyOf(substitutedBody));
            case PUT -> builder.PUT(bodyOf(substitutedBody));
            case PATCH -> builder.method("PATCH", bodyOf(substitutedBody));
            default -> {
                // fallback for rare HTTP methods
                if (substitutedBody == null) {
                    builder.method(method.name(), HttpRequest.BodyPublishers.noBody());
                } else {
                    builder.method(method.name(), HttpRequest.BodyPublishers.ofString(substitutedBody));
                }
            }
        }

        // If body exists but user did not set Content-Type → guess automatically
        if (substitutedBody != null && !hasHeader(step.getHeaders(), "Content-Type")) {
            String trimmed = substitutedBody.stripLeading();
            if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                builder.header("Content-Type", "application/json");
            } else {
                builder.header("Content-Type", "text/plain; charset=utf-8");
            }
        }

        return builder.build();
    }

    // ---------------------------------------------------
    // Helpers
    // ---------------------------------------------------

    private static HttpRequest.BodyPublisher bodyOf(String body) {
        return body == null
                ? HttpRequest.BodyPublishers.ofString("")
                : HttpRequest.BodyPublishers.ofString(body);
    }

    private static boolean hasHeader(Map<String, String> headers, String target) {
        if (headers == null) return false;
        for (String k : headers.keySet()) {
            if (k != null && k.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private static String buildUrlWithQueryParams(
            String baseUrl,
            Map<String, String> params,
            ExecutionContext ctx
    ) throws InvalidHttpRequestException {

        if (params == null || params.isEmpty()) return baseUrl;

        StringBuilder sb = new StringBuilder(baseUrl);
        boolean hasQuestion = baseUrl.contains("?");

        for (var entry : params.entrySet()) {
            String rawKey = substitute(entry.getKey(), ctx);
            String rawVal = substitute(entry.getValue(), ctx);

            if (rawKey == null) {
                // If user forgot variable, this is user-side error
                throw new InvalidHttpRequestException("Query param name resolved to null: " + entry.getKey());
            }

            String k = URLEncoder.encode(rawKey, StandardCharsets.UTF_8);
            String v = rawVal == null ? "" :
                    URLEncoder.encode(rawVal, StandardCharsets.UTF_8);

            sb.append(hasQuestion ? '&' : '?');
            hasQuestion = true;

            sb.append(k).append('=').append(v);

        }

        return sb.toString();
    }

    /**
     * Substitute {{var}} using ctx.variables.
     * Unknown variables → replaced with empty string.
     */
    private static String substitute(String input, ExecutionContext ctx) {
        if (input == null || ctx == null) return input;

        Matcher matcher = PLACEHOLDER.matcher(input);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String var = matcher.group(1);
            if (!ctx.getVariables().containsKey(var)) {
                throw new InvalidHttpRequestException("Variable not found: " + var);
            }

            Object value = ctx.getVariables().get(var);

            // null → literal null (without quotes)
            String replacement = (value == null) ? "null" : value.toString();
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

}
