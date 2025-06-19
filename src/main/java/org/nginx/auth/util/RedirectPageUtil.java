package org.nginx.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author dongpo.li
 * @date 2025/1/9 18:57
 */
public class RedirectPageUtil {

    public static String buildRedirectUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        String requestPath = request.getServletPath();
        String redirect = requestPath + (StringUtils.isBlank(query) ? "" : "?" + query);
        return URLEncoder.encode(redirect, StandardCharsets.UTF_8);
    }

    public static String resolveRedirectUrl(String redirect) {
//        String contextPath = exchange.getRequest().getPath().contextPath().value();
        redirect = URLDecoder.decode(redirect, StandardCharsets.UTF_8);
//        if (withContextPath) {
//            redirect = StringUtils.substringAfter(redirect, contextPath);
//        }

        return redirect;
    }

}
