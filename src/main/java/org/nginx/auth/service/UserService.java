package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.crypto.Crypto;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageSenderService messageSenderService;

    public User selectByAccessKey(String accessKey) {
        if (StringUtils.isBlank(accessKey)) {
            return null;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getAccessKey, accessKey);

        return userRepository.selectOne(queryWrapper);
    }

    public void create(User user) {
        userRepository.insert(user);
    }

    public User selectByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);

        return userRepository.selectOne(queryWrapper);
    }

    @Transactional
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

        String accessKey = UserUtil.generateUserAccessKey();
        String accessKeyHash = DigestUtils.sha256Hex(accessKey);
        User user = selectByAccessKey(accessKeyHash);
        // 确保生成的 license 是唯一的
        while (user != null) {
            accessKey = UserUtil.generateUserAccessKey();
            accessKeyHash = DigestUtils.sha256Hex(accessKey);
            user = selectByAccessKey(accessKeyHash);
        }

        // 创建新用户
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setAccessKey(accessKeyHash); // 注册时不设置密码
        newUser.setBlocked(false);

        int insertResult = userRepository.insert(newUser);
        if (insertResult <= 0) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "Failed to register user";
        }

        try {
            String subject = "Welcome to Our Service";
            String content = "Thank you for registering! Your accessKey is: " + accessKey;
            messageSenderService.sendHtml(email, subject, content);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


        return "success";
    }

}
