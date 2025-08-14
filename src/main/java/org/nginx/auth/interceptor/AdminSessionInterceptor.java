package org.nginx.auth.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Set;

@Component
public class AdminSessionInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AdminSessionInterceptor.class);

    private static Set<String> adminSet;

    @Value("${admin.list}")
    private String adminListVal;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {
        if (StringUtils.isNotBlank(adminListVal)) {
            String[] adminArray = adminListVal.split(",");

            String[] adminAccessKeyHashList = new String[adminArray.length];
            for (int i = 0; i < adminArray.length; i++) {
                String adminAccessKey = adminArray[i].trim();
                if (StringUtils.isNotBlank(adminAccessKey)) {
                    adminAccessKeyHashList[i] = DigestUtils.sha256Hex(adminAccessKey);
                } else {
                    adminAccessKeyHashList[i] = null;
                }
            }

            adminSet = Set.of(adminAccessKeyHashList);
        } else {
            adminSet = Set.of();
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId);
        // 只查询一条数据就足够了，避免性能问题
        Page<User> page = new Page<>(1, 1);
        List<User> existUserList = userRepository.selectList(page, queryWrapper);
        if (CollectionUtils.isEmpty(existUserList)) {
            // 如果数据库中没有用户，则添加一个默认的管理员用户
            User adminUser = new User();
            String accessKey = UserUtil.generateUserAccessKey();
            logger.info("检测到没有管理员用户,创建管理员用户, accessKey: {}", accessKey);
            logger.info("请将以上accessKey配置在[admin.list]中并重启服务以使管理员账户生效");
            logger.info("请注意先保存accessKey,该accessKey只会展示一次,且后续无法恢复");
            String accessKeyHash = DigestUtils.sha256Hex(accessKey);
            adminUser.setAccessKey(accessKeyHash);
            userRepository.insert(adminUser);
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
        if (user == null || !adminSet.contains(user.getAccessKey())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }

    public static Set<String> getAdminSet() {
        return adminSet;
    }

}

