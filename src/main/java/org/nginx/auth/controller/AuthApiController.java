package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.service.AuthApiService;
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
    private AuthApiService authApiService;

    @RequestMapping("/access")
    public String access(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(BasicConstant.CURRENT_USER_SESSION_KEY);
        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "401 Authorization Required";
        }

        String rst = authApiService.access(user, response);

        return rst;
    }

}
