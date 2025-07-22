package org.nginx.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nginx.auth.model.SubscriptionInfo;
import org.nginx.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthApiService {

    @Autowired
    private SubscriptionInfoService subscriptionInfoService;

    public String access(HttpServletRequest request, HttpServletResponse response, User user) {

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "401 Authorization Required";
        }

        SubscriptionInfo subscriptionInfo = subscriptionInfoService.selectByUserId(user.getId());
        if (subscriptionInfo == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "403 Forbidden";
        }

        if (subscriptionInfo.getSubscribeExpireTime() == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "403 Forbidden";
        }

        if (subscriptionInfo.getSubscribeExpireTime().before(new java.util.Date())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "403 Forbidden";
        }

        String serverName = request.getServerName();
        // 鉴权要请求的真实URI,不是鉴权本身的URL
        String requestURI = request.getHeader("X-Original-URI");




        return "success";
    }

}
