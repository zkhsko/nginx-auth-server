package org.nginx.auth.interceptor;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class AdminSessionInterceptor implements HandlerInterceptor {

    @Value("${admin.list}")
    private String adminListVal;

    private static Set<String> adminSet;

    @PostConstruct
    public void init() {
        if (StringUtils.isNotBlank(adminListVal)) {
            String[] adminArray = adminListVal.split(",");
            adminSet = Set.of(adminArray);
        } else {
            adminSet = Set.of();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        User user = (User) session.getAttribute(BasicConstant.CURRENT_USER_SESSION_KEY);
        if (user == null || !adminSet.contains(user.getEmail())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }

    public static Set<String> getAdminSet() {
        return adminSet;
    }

}

