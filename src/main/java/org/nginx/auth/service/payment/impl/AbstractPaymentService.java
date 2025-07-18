package org.nginx.auth.service.payment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.nginx.auth.enums.OrderInfoStatusEnum;
import org.nginx.auth.enums.OrderRefundInfoStatusEnum;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.OrderRefundInfo;
import org.nginx.auth.repository.OrderInfoRepository;
import org.nginx.auth.repository.OrderPaymentInfoRepository;
import org.nginx.auth.repository.OrderRefundInfoRepository;
import org.nginx.auth.service.OrderRefundInfoService;
import org.nginx.auth.service.SubscriptionInfoService;
import org.nginx.auth.service.payment.PaymentService;
import org.nginx.auth.util.OrderInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public abstract class AbstractPaymentService implements PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPaymentService.class);

    @Autowired
    private OrderInfoRepository orderInfoRepository;
    @Autowired
    private OrderPaymentInfoRepository orderPaymentInfoRepository;
    @Autowired
    private SubscriptionInfoService subscriptionInfoService;
    @Autowired
    private OrderRefundInfoService orderRefundInfoService;
    @Autowired
    private OrderRefundInfoRepository orderRefundInfoRepository;

    @Override
    public void onPaymentSuccess(OrderPaymentInfo orderPaymentInfo) {
        // 支付记录
        orderPaymentInfoRepository.insert(orderPaymentInfo);


        // 修改订单状态为已支付
        LambdaUpdateWrapper<OrderInfo> orderInfoUpdate = new LambdaUpdateWrapper<>();
        orderInfoUpdate.eq(OrderInfo::getOrderId, orderPaymentInfo.getOrderId())
                .set(OrderInfo::getOrderStatus, OrderInfoStatusEnum.PAYMENT_SUCCESS.name())
                .set(OrderInfo::getOrderPaymentInfoId, orderPaymentInfo.getId());
        orderInfoRepository.update(orderInfoUpdate);


        // 延长订阅时间
        subscriptionInfoService.refreshExpireAt(orderPaymentInfo);
    }

    public void onRefundSuccess(OrderRefundInfo orderRefundInfo) {

        // 查询对应的退款记录
        // 如果有,说明是从后台退款的
        // 如果没有,说明是是从支付宝后台发起的退款
        OrderRefundInfo existsOrderRefundInfo =
                orderRefundInfoService.selectByRefundOrderId(orderRefundInfo.getRefundOrderId());

        if (existsOrderRefundInfo != null) {
            // 修改退款记录状态为已退款
            LambdaUpdateWrapper<OrderRefundInfo> orderRefundInfoUpdate = new LambdaUpdateWrapper<>();
            orderRefundInfoUpdate.eq(OrderRefundInfo::getRefundOrderId, orderRefundInfo.getRefundOrderId());
            orderRefundInfoUpdate.eq(OrderRefundInfo::getStatus, OrderRefundInfoStatusEnum.TRADE_REFUND_PENDING.name());
            orderRefundInfoUpdate.set(OrderRefundInfo::getOutBizNo, orderRefundInfo.getOutBizNo());
            orderRefundInfoUpdate.set(OrderRefundInfo::getStatus, OrderRefundInfoStatusEnum.TRADE_REFUND_SUCCESS.name());
            orderRefundInfoUpdate.set(OrderRefundInfo::getOrderRefundAmount, orderRefundInfo.getOrderRefundAmount());
            orderRefundInfoUpdate.set(OrderRefundInfo::getOrderRefundTime, orderRefundInfo.getOrderRefundTime());
            int updated = orderRefundInfoRepository.update(orderRefundInfoUpdate);
            if (updated < 1) {
                // 没有修改成功
                return;
            }

            if (existsOrderRefundInfo.getReturnPurchase() != null && existsOrderRefundInfo.getReturnPurchase()) {
                // 退还购买的商品
                LambdaQueryWrapper<OrderInfo> orderInfoQuery = new LambdaQueryWrapper<>();
                orderInfoQuery.eq(OrderInfo::getOrderId, existsOrderRefundInfo.getOrderId());

                OrderInfo orderInfo = orderInfoRepository.selectOne(orderInfoQuery);

                boolean premiumPlanReturned = false;
                if (orderInfo.getPremiumPlanReturned() != null && !orderInfo.getPremiumPlanReturned()) {
                    // 退还购买的商品
                    subscriptionInfoService.refund(orderInfo.getOrderId());
                    premiumPlanReturned = true;
                }

                changeOrderStatusToRefunded(orderInfo, orderRefundInfo, premiumPlanReturned);


            }


        } else {
            // 没有对应的退款记录

            LambdaQueryWrapper<OrderInfo> orderInfoQuery = new LambdaQueryWrapper<>();
            orderInfoQuery.eq(OrderInfo::getOrderId, orderRefundInfo.getOrderId());

            // 创建退款记录
            String refundOrderId = "";


            OrderInfo orderInfo = orderInfoRepository.selectOne(orderInfoQuery);
            if (orderInfo == null) {
                // 没有对应的订单记录
                logger.error("退款通知没有找到对应的订单记录, orderId={}, outBizNo={}",
                        orderRefundInfo.getOrderId(), orderRefundInfo.getOutBizNo());
                refundOrderId = OrderInfoUtils.generateOrderId(0L);
            } else {
                refundOrderId = OrderInfoUtils.generateOrderId(orderInfo.getUserId());
            }

            orderRefundInfo.setRefundOrderId(refundOrderId);
            orderRefundInfo.setStatus(OrderRefundInfoStatusEnum.TRADE_REFUND_SUCCESS.name());
            orderRefundInfoRepository.insert(orderRefundInfo);

            // 这种情况不需要退订阅.如果需要的话,管理员再操作一次吧.这种情况应该比较少见

            // 如果有对应订单的话,修改主订单状态
            if (orderInfo != null) {
                changeOrderStatusToRefunded(orderInfo, orderRefundInfo, false);
            }

        }


    }

    private void changeOrderStatusToRefunded(OrderInfo orderInfo, OrderRefundInfo orderRefundInfo, Boolean premiumPlanReturned) {
        LambdaUpdateWrapper<OrderInfo> orderInfoUpdate = new LambdaUpdateWrapper<>();
        orderInfoUpdate.eq(OrderInfo::getOrderId, orderInfo.getOrderId());

        if (orderRefundInfo.getOrderRefundAmount().equals(orderInfo.getOrderAmount())) {
            orderInfoUpdate.set(OrderInfo::getOrderStatus, OrderInfoStatusEnum.TRADE_CLOSED.name());
            orderInfoRepository.update(orderInfoUpdate);
            return;
        }

        LambdaQueryWrapper<OrderRefundInfo> refundQueryWrapper = new LambdaQueryWrapper<>();
        refundQueryWrapper.eq(OrderRefundInfo::getOrderId, orderInfo.getOrderId());
        refundQueryWrapper.eq(OrderRefundInfo::getStatus, OrderInfoStatusEnum.TRADE_REFUND_SUCCESS.name());
        List<OrderRefundInfo> existingRefundList = orderRefundInfoRepository.selectList(refundQueryWrapper);
        long totalRefundedAmount = existingRefundList.stream()
                .mapToLong(OrderRefundInfo::getOrderRefundAmount)
                .sum();
        if (totalRefundedAmount == orderInfo.getOrderAmount()) {
            orderInfoUpdate.set(OrderInfo::getOrderStatus, OrderInfoStatusEnum.TRADE_CLOSED.name());
        } else {
            orderInfoUpdate.set(OrderInfo::getOrderStatus, OrderInfoStatusEnum.TRADE_REFUND_SUCCESS.name());
        }

        if (premiumPlanReturned) {
            orderInfoUpdate.set(OrderInfo::getPremiumPlanReturned, true);
        }


        orderInfoRepository.update(orderInfoUpdate);
    }

}
