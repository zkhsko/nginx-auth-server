package org.nginx.auth.util;

import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.interceptor.AdminSessionInterceptor;

import java.util.Set;

public class UserUtil {

    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateUserAccessKey() {
        // 64位随机字符串作为用户的accessKey,包含数字和大小写字母
        StringBuilder accessKey = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int index = (int) (Math.random() * characters.length());
            accessKey.append(characters.charAt(index));
        }
        return accessKey.toString();
    }

    public static boolean isAdmin(String accessKey) {
        if (StringUtils.isBlank(accessKey)) {
            return false;
        }

        Set<String> adminSet = AdminSessionInterceptor.getAdminSet();
        if (adminSet == null || adminSet.isEmpty()) {
            return false;
        }

        return adminSet.contains(accessKey);
    }

}
