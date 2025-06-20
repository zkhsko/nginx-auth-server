package org.nginx.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author dongpo.li
 * @date 2023/12/19
 */
@Controller
@RequestMapping("/account")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${access.token.key}")
    private String accessTokenKey;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "index";
    }

    @GetMapping("/login.html")
    public String loginPage() {
        return "login.html";
    }

    @PostMapping("/login.html")
    public String loginAction(String username, HttpServletRequest request, HttpServletResponse response, Model model) {

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getLicense, username);
        User user = userRepository.selectOne(queryWrapper);
        if (user == null) {
            model.addAttribute("licenseText", username);
            model.addAttribute("licenseNotFountError", "License invalid");
            return "login.html";
        }

        HttpSession session = request.getSession();
        session.setAttribute(BasicConstant.CURRENT_USER_SESSION_KEY, user);

        String browserUserAgent = request.getHeaders("User-Agent").nextElement();

        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", request.getContextPath() + "/account/login/success.html");

        return "login.html";

    }

    @RequestMapping("/login/success.html")
    public String loginSuccess() {
        return "login-success.html";
    }

    @RequestMapping("/403.html")
    public String access403() {
        return "403.html";
    }


    @RequestMapping("/logout.html")
    @ResponseBody
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession();
        session.removeAttribute(BasicConstant.CURRENT_USER_SESSION_KEY);

        return "logout.html";
    }

}
