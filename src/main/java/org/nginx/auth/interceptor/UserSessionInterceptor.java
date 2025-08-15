package org.nginx.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nginx.auth.model.User;
import org.nginx.auth.util.SessionUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();

        String loginPage = "/user/login.html";

        // 这个列表实际上在配置好nginx之后是没有什么用的
        // 甚至整个拦截器都是没有什么用的
        List<String> excludeUrlList = new ArrayList<>();
        excludeUrlList.add(contextPath + "/res/");
        excludeUrlList.add(contextPath + loginPage);
//        excludeUrlList.add(contextPath + "/user/register.html");
        excludeUrlList.add(contextPath + "/api/v1.0/auth/access");
        excludeUrlList.add(contextPath + "/anonymous/");
        // 下单可以不登录
        excludeUrlList.add(contextPath + "/user/order/confirm.html");
        excludeUrlList.add(contextPath + "/user/order/create");

        for (String url : excludeUrlList) {
            if (requestURI.startsWith(url)) {
                return true; // Allow access to excluded URLs
            }
        }

        // Check if the user is logged in
        // If not logged in, redirect to login page
        User user = SessionUtil.getCurrentUser(request);
        if (user == null) {
            response.sendRedirect(contextPath + loginPage);
            return false; // Prevent further processing
        }

        return true;
    }

}

