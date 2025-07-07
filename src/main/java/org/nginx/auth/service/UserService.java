package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User selectByLicense(String license) {
        if (license == null || license.isEmpty()) {
            return null;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getLicense, license);

        return userRepository.selectOne(queryWrapper);
    }

    public User selectByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);

        return userRepository.selectOne(queryWrapper);
    }

    public String register(String email, String code, HttpServletResponse response) {
        // TODO: 验证码校验逻辑，暂时模拟校验通过
        if (email == null || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Email is required";
        }
        if (code == null || code.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Verification code is required";
        }

        // 模拟验证码校验通过
        // TODO: 添加验证码,否则可能会被恶意注册

        // 检查邮箱是否已注册
        User existsUser = selectByEmail(email);
        if (existsUser != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return "Email already registered";
        }

        String license = UserUtil.generateUserLicense();
        User user = selectByLicense(license);
        // 确保生成的 license 是唯一的
        while (user != null) {
            license = UserUtil.generateUserLicense();
            user = selectByLicense(license);
        }

        // 创建新用户
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setLicense(license); // 注册时不设置密码
        newUser.setBlocked(false);

        int insertResult = userRepository.insert(newUser);
        if (insertResult <= 0) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "Failed to register user";
        }

        // TODO: 发送注册成功邮件通知用户

        return "success";
    }

}
