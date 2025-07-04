package org.nginx.auth.util;

import org.nginx.auth.model.OrderSkuInfo;
import org.nginx.auth.model.PremiumPlan;
import org.springframework.cglib.beans.BeanCopier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author dongpo.li
 * @date 2023/12/20
 */
public class OrderInfoUtils {

    public static final BeanCopier PREMIUM_PLAN_2_ORDER_SKU_INFO = BeanCopier.create(PremiumPlan.class, OrderSkuInfo.class, false);

    /**
     * 生成订单号,规则如下
     * 时间戳 + 用户id的hashcode + 3位随机数
     * 时间戳 + 用户id基本可以保证唯一,但是会泄露用户id,所以改为string形式的hashcode
     * 3位随机数有两个作用
     * 一是防止同一用户同一时间下单,订单号重复
     * 二是防止不同用户hashcode相同,同时下单的话订单号重复
     * @return
     */
    public static String generateOrderId(Long userId) {

        String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String userIdHashCode = String.format("%010d", Math.abs(String.valueOf(userId).hashCode()));

        String random = String.format("%03d", (int) (Math.random() * 1000));
        return datetime + userIdHashCode + random;
    }



}
