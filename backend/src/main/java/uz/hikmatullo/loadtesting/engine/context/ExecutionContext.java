package uz.hikmatullo.loadtesting.engine.context;

import lombok.Getter;
import lombok.Setter;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
*
*
* */
@Getter
@Setter
public class ExecutionContext {

    private final Map<String, Object> variables = new ConcurrentHashMap<>();

    // One CookieManager per virtual user => cookie isolation
    private final CookieManager cookieManager;

    // HttpClient tied to the cookie manager
    private final HttpClient httpClient;

    public ExecutionContext() {
        this.cookieManager = new CookieManager();
        // Accept cookies by default;
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(30))
                .cookieHandler(cookieManager) // the important bit
                .build();
    }

    public void clearCookies() {
        cookieManager.getCookieStore().removeAll();
    }

    public java.util.List<java.net.HttpCookie> getCookies() {
        return cookieManager.getCookieStore().getCookies();
    }

    public String getCookieValue(String name) {
        return cookieManager.getCookieStore().getCookies().stream()
                .filter(c -> c.getName().equals(name))
                .map(java.net.HttpCookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
