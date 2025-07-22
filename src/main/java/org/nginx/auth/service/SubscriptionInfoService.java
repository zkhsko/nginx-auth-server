package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.nginx.auth.model.*;
import org.nginx.auth.repository.OrderInfoRepository;
import org.nginx.auth.repository.OrderSkuInfoRepository;
import org.nginx.auth.repository.SubscriptionInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author dongpo.li
 * @date 2024/12/23 15:27
 */
@Service
public class SubscriptionInfoService {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionInfoService.class);

    @Autowired
    private OrderInfoRepository orderInfoRepository;
    @Autowired
    private OrderSkuInfoRepository orderSkuInfoRepository;
    @Autowired
    private SubscriptionInfoRepository subscriptionInfoRepository;

    public List<SubscriptionInfo> selectByUserId(Long userId) {
        LambdaQueryWrapper<SubscriptionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscriptionInfo::getUserId, userId);
        queryWrapper.orderByDesc(true, SubscriptionInfo::getId);
        return subscriptionInfoRepository.selectList(queryWrapper);
    }

    public SubscriptionInfo selectByUserIdAndPremiumPlanId(Long userId, Long premiumPlanId) {
        LambdaQueryWrapper<SubscriptionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SubscriptionInfo::getUserId, userId);
        queryWrapper.eq(SubscriptionInfo::getPremiumPlanId, premiumPlanId);
        return subscriptionInfoRepository.selectOne(queryWrapper);
    }

    /**
     * 根据支付流水号刷新用户订阅到期时间
     *
     * @param orderPaymentInfo 支付记录
     */
    public void refreshExpireAt(OrderPaymentInfo orderPaymentInfo) {

        LambdaQueryWrapper<OrderInfo> orderInfoQuery = new LambdaQueryWrapper<>();
        orderInfoQuery.eq(OrderInfo::getOrderId, orderPaymentInfo.getOrderId());
        OrderInfo orderInfo = orderInfoRepository.selectOne(orderInfoQuery);

        String orderId = orderInfo.getOrderId();
        Long userId = orderInfo.getUserId();

        // 查询订单商品列表
        List<OrderSkuInfo> skuList = orderSkuInfoRepository.selectList(
                new LambdaQueryWrapper<OrderSkuInfo>()
                        .eq(OrderSkuInfo::getOrderId, orderId)
        );

        if (skuList == null || skuList.isEmpty()) {
            logger.warn("No order sku found for orderId: {}", orderId);
            return;
        }

        // TODO: 暂时每次只能买一个产品
        Long premiumPlanId = skuList.get(0).getPremiumPlanId();

        // 计算总订阅时间（单位和时间值乘以数量）
        long totalDays = 0;
        long totalMonths = 0;
        long totalYears = 0;

        for (OrderSkuInfo sku : skuList) {
            long cnt = sku.getCnt() == null ? 1 : sku.getCnt();
            String unit = sku.getPremiumPlanTimeUnit();
            Long value = sku.getPremiumPlanTimeValue() == null ? 0 : sku.getPremiumPlanTimeValue();

            if (unit == null || value == 0) {
                continue;
            }

            switch (unit) {
                case "DAY":
                    totalDays += value * cnt;
                    break;
                case "MONTH":
                    totalMonths += value * cnt;
                    break;
                case "YEAR":
                    totalYears += value * cnt;
                    break;
                default:
                    logger.warn("Unknown time unit {} for sku id {}", unit, sku.getId());
            }
        }

        // 查询当前用户订阅信息
        SubscriptionInfo subscriptionInfo = selectByUserIdAndPremiumPlanId(userId, premiumPlanId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseTime;

        if (subscriptionInfo == null) {
            // 第一次订阅，创建记录，从当前时间开始计算
            subscriptionInfo = new SubscriptionInfo();
            subscriptionInfo.setUserId(userId);
            baseTime = now;
        } else {
            Date expireDate = subscriptionInfo.getSubscribeExpireTime();
            if (expireDate == null) {
                baseTime = now;
            } else {
                LocalDateTime expireTime = LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.systemDefault());
                // 如果当前订阅到期时间在当前时间之后，从订阅到期时间开始延长
                // 否则从当前时间开始延长
                baseTime = expireTime.isAfter(now) ? expireTime : now;
            }
        }

        // 计算新的订阅到期时间
        LocalDateTime newExpireTime =
                baseTime
                        .plusYears(totalYears)
                        .plusMonths(totalMonths)
                        .plusDays(totalDays);

        Date newExpireDate = Date.from(newExpireTime.atZone(ZoneId.systemDefault()).toInstant());

        if (subscriptionInfo.getId() == null) {
            // 如果没有订阅记录，创建新的订阅记录
            SubscriptionInfo subscriptionInfoInsert = new SubscriptionInfo();
            subscriptionInfoInsert.setUserId(userId);
            subscriptionInfoInsert.setSubscribeExpireTime(newExpireDate);
            subscriptionInfoRepository.insert(subscriptionInfoInsert);
            logger.info("Created new subscription for userId {} with expire time {}", userId, newExpireDate);
        } else {
            LambdaUpdateWrapper<SubscriptionInfo> subscriptionInfoUpdate = new LambdaUpdateWrapper<>();
            subscriptionInfoUpdate.set(SubscriptionInfo::getSubscribeExpireTime, newExpireDate);
            subscriptionInfoUpdate.eq(SubscriptionInfo::getId, subscriptionInfo.getId());
            subscriptionInfoRepository.update(subscriptionInfoUpdate);
            logger.info("Updated subscription for userId {} with new expire time {}", userId, newExpireDate);
        }
    }

    public void refund(String orderId) {

        // 查询订单信息，获取用户ID
        OrderInfo orderInfo = orderInfoRepository.selectOne(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getOrderId, orderId)
        );
        if (orderInfo == null) {
            logger.warn("OrderInfo not found for orderId: {}", orderId);
            return;
        }
        Long userId = orderInfo.getUserId();

        // 查询订单商品列表
        List<OrderSkuInfo> skuList = orderSkuInfoRepository.selectList(
                new LambdaQueryWrapper<OrderSkuInfo>()
                        .eq(OrderSkuInfo::getOrderId, orderId)
        );
        if (skuList == null || skuList.isEmpty()) {
            logger.warn("No order sku found for orderId: {}", orderId);
            return;
        }

        // TODO: 暂时每次只能买一个产品
        Long premiumPlanId = skuList.get(0).getPremiumPlanId();

        // 查询当前用户订阅信息
        SubscriptionInfo subscriptionInfo = selectByUserIdAndPremiumPlanId(userId, premiumPlanId);
        if (subscriptionInfo == null) {
            logger.warn("SubscriptionInfo not found for userId: {}", userId);
            return;
        }

        Date expireDate = subscriptionInfo.getSubscribeExpireTime();
        if (expireDate == null) {
            logger.warn("SubscribeExpireTime is null for userId: {}", userId);
            return;
        }
        LocalDateTime baseTime = LocalDateTime.ofInstant(expireDate.toInstant(), ZoneId.systemDefault());

        // 计算扣减时间
        long totalDays = 0;
        long totalMonths = 0;
        long totalYears = 0;

        for (OrderSkuInfo sku : skuList) {
            long cnt = sku.getCnt() == null ? 1 : sku.getCnt();
            String unit = sku.getPremiumPlanTimeUnit();
            Long value = sku.getPremiumPlanTimeValue() == null ? 0 : sku.getPremiumPlanTimeValue();

            if (unit == null || value == 0) {
                continue;
            }

            switch (unit) {
                case "DAY":
                    totalDays += value * cnt;
                    break;
                case "MONTH":
                    totalMonths += value * cnt;
                    break;
                case "YEAR":
                    totalYears += value * cnt;
                    break;
                default:
                    logger.warn("Unknown time unit {} for sku id {}", unit, sku.getId());
            }
        }

        // 扣减时间
        LocalDateTime newExpireTime =
                baseTime
                        .minusYears(totalYears)
                        .minusMonths(totalMonths)
                        .minusDays(totalDays);

        Date newExpireDate = Date.from(newExpireTime.atZone(ZoneId.systemDefault()).toInstant());

        // 更新订阅信息
        LambdaUpdateWrapper<SubscriptionInfo> subscriptionInfoUpdate = new LambdaUpdateWrapper<>();
        subscriptionInfoUpdate.set(SubscriptionInfo::getSubscribeExpireTime, newExpireDate);
        subscriptionInfoUpdate.eq(SubscriptionInfo::getId, subscriptionInfo.getId());
        subscriptionInfoRepository.update(subscriptionInfoUpdate);

        logger.info("Refund processed for userId {} with new expire time {}", userId, newExpireDate);




    }

}
