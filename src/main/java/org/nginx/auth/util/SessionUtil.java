package org.nginx.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtil {

    public static User getCurrentUser() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null; // No request context available
        }
        HttpServletRequest request = attrs.getRequest();
        return getCurrentUser(request);
    }

    public static User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null; // No session available
        }
        return (User) session.getAttribute(BasicConstant.CURRENT_USER_SESSION_KEY);
    }

    public static void setCurrentUser(User user) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        HttpServletRequest request = attrs.getRequest();
        HttpSession session = request.getSession();
        if (session != null) {
            session.setAttribute(BasicConstant.CURRENT_USER_SESSION_KEY, user);
        }
    }

}
