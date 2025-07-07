package org.nginx.auth.util;

import org.nginx.auth.interceptor.AdminSessionInterceptor;

import java.util.Set;

public class UserUtil {

    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateUserLicense() {
        // 64位随机字符串作为用户的license,包含数字和大小写字母
        StringBuilder license = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int index = (int) (Math.random() * characters.length());
            license.append(characters.charAt(index));
        }
        return license.toString();
    }

    public static boolean isAdmin(String email) {
        Set<String> adminSet = AdminSessionInterceptor.getAdminSet();
        if (adminSet == null || adminSet.isEmpty()) {
            return false;
        }

        return adminSet.contains(email);
    }

}
