package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.nginx.auth.service.UserService;
import org.nginx.auth.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index() {
        return "redirect:/user/order/index.html";
    }

    @GetMapping("/login.html")
    public String loginPage() {
        return "login.html";
    }

    @PostMapping("/login.html")
    public String loginAction(String username, HttpServletRequest request, HttpServletResponse response, Model model) {

        User user = userService.selectByLicense(username);
        if (user == null) {
            model.addAttribute("licenseText", username);
            model.addAttribute("licenseNotFountError", "License invalid");
            return "login.html";
        }

        HttpSession session = request.getSession();
        session.setAttribute(BasicConstant.CURRENT_USER_SESSION_KEY, user);

        String browserUserAgent = request.getHeaders("User-Agent").nextElement();

        boolean admin = UserUtil.isAdmin(user.getEmail());
        if (admin) {
            return "redirect:/admin/premium-plan/index.html";
        } else {
            return "redirect:/anonymous/premium-plan/index.html";
        }


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

    @GetMapping("/register.html")
    public String registerPage() {
        return "register.html";
    }

    @PostMapping("/register.html")
    public String register(String email, String code, HttpServletResponse response) {
        String result = userService.register(email, code, response);

        if (!"success".equals(result)) {
            return "register.html";
        } else {
            return "redirect:/user/login.html";
        }


    }

}
