package uz.hikmatullo.loadtesting.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public class Util {

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    public static String getCurrentRequestIp() {
        return getCurrentRequest().getRemoteAddr();
    }

}
