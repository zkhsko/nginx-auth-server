package org.nginx.auth.interceptor;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.nginx.auth.util.SessionUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class UserSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();

        String loginPage = "/user/login.html";

        List<String> excludeUrlList = new ArrayList<>();
        excludeUrlList.add(contextPath + "/res/");
        excludeUrlList.add(contextPath + loginPage);
        excludeUrlList.add(contextPath + "/user/register.html");
        excludeUrlList.add(contextPath + "/anonymous/premium-plan/index.html");

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

