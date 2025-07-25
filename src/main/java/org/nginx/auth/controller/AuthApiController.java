package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.service.AuthApiService;
import org.nginx.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/v1.0/auth")
@RestController
public class AuthApiController {

    @Autowired
    private AuthApiService authApiService;
    @Autowired
    private UserService userService;

    @RequestMapping("/access")
    public String access(HttpServletRequest request, HttpServletResponse response) {


        List<GetCurrUser> getCurrUserList = new ArrayList<>();
        getCurrUserList.add(new GetCurrUserImpl1());
        getCurrUserList.add(new GetCurrUserImpl2(userService));
        getCurrUserList.add(new GetCurrUserImpl3(userService));

        User user = null;
        // 依次尝试根据对应的方法获取当前用户
        // 如果取到了直接返回
        for (GetCurrUser getCurrUser : getCurrUserList) {
            user = getCurrUser.getCurrUser(request);
            if (user != null) {
                break;
            }
        }

        // 最终没有取到返回401
        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return "401 Authorization Required";
        }


        String rst = authApiService.access(request, response, user);

        return rst;
    }

    private static abstract class GetCurrUser {

        abstract User getCurrUser(HttpServletRequest request);

    }

    /**
     * 普通网页请求
     */
    private static final class GetCurrUserImpl1 extends GetCurrUser {

        @Override
        User getCurrUser(HttpServletRequest request) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(BasicConstant.CURRENT_USER_SESSION_KEY);
            return user;
        }

    }

    /**
     * 兼容Google API请求
     */
    private static final class GetCurrUserImpl2 extends GetCurrUser {

        private final UserService userService;

        public GetCurrUserImpl2(UserService userService) {
            this.userService = userService;
        }

        @Override
        User getCurrUser(HttpServletRequest request) {
            String license = request.getHeader("X-goog-api-key");
            if (StringUtils.isNotBlank(license)) {
                User user = userService.selectByLicense(license);
                if (user != null) {
                    return user;
                }
            }
            return null;
        }
    }

    /**
     * 兼容OpenAI API请求
     */
    private static final class GetCurrUserImpl3 extends GetCurrUser {

        private final UserService userService;

        public GetCurrUserImpl3(UserService userService) {
            this.userService = userService;
        }

        @Override
        User getCurrUser(HttpServletRequest request) {
            String authorizationValue = request.getHeader("Authorization");
            if (StringUtils.isNotBlank(authorizationValue)) {
                String license = StringUtils.removeStartIgnoreCase(authorizationValue, "Bearer ");
                if (StringUtils.isNotBlank(license)) {
                    User user = userService.selectByLicense(license);
                    if (user != null) {
                        return user;
                    }
                }

            }

            return null;
        }
    }


}
