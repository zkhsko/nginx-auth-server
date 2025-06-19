package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.AccountInfo;
import org.nginx.auth.repository.AccountInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1.0/auth")
@RestController
public class AuthApiController {

    @Value("${access.token.key}")
    private String accessTokenKey;

    @Autowired
    private AccountInfoRepository accountInfoRepository;

    @RequestMapping("/access")
    public String access(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        AccountInfo accountInfo = (AccountInfo) session.getAttribute(BasicConstant.CURRENT_USER_SESSION_KEY);
        if (accountInfo == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "401 Authorization Required";
        }

        return "success";
    }

}
