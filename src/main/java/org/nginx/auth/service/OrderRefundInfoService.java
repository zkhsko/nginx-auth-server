package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.nginx.auth.enums.OrderInfoStatusEnum;
import org.nginx.auth.enums.OrderRefundInfoStatusEnum;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.OrderRefundInfo;
import org.nginx.auth.repository.OrderInfoRepository;
import org.nginx.auth.repository.OrderPaymentInfoRepository;
import org.nginx.auth.repository.OrderRefundInfoRepository;
import org.nginx.auth.service.payment.PaymentService;
import org.nginx.auth.service.payment.PaymentServiceFactory;
import org.nginx.auth.util.OrderInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderRefundInfoService {

    @Autowired
    private OrderRefundInfoRepository orderRefundInfoRepository;
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    @Autowired
    private OrderPaymentInfoRepository orderPaymentInfoRepository;

    public OrderRefundInfo selectByRefundOrderId(String refundOrderId) {
        LambdaQueryWrapper<OrderRefundInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderRefundInfo::getRefundOrderId, refundOrderId);
        return orderRefundInfoRepository.selectOne(queryWrapper);
    }

    public List<OrderRefundInfo> selectListByOrderId(String orderId) {
        LambdaQueryWrapper<OrderRefundInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderRefundInfo::getOrderId, orderId);
        return orderRefundInfoRepository.selectList(queryWrapper);
    }

    @Transactional
    public String refundByAdmin(String orderId, Long orderRefundAmount, String refundReason, Boolean returnPurchase) {
        // 查询订单信息
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderId, orderId);
        OrderInfo orderInfo = orderInfoRepository.selectOne(queryWrapper);
        if (orderInfo == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        // 查询订单的支付记录
        if (orderInfo.getOrderPaymentInfoId() == null) {
            throw new IllegalArgumentException("订单没有支付记录，无法退款");
        }
        OrderPaymentInfo orderPaymentInfo = orderPaymentInfoRepository.selectById(orderInfo.getOrderPaymentInfoId());
        if (orderPaymentInfo == null) {
            throw new IllegalArgumentException("订单支付记录不存在，无法退款");
        }
        Set<String> availableRefundTradeStatus = Set.of(
                OrderInfoStatusEnum.PAYMENT_SUCCESS.name(),
                OrderInfoStatusEnum.TRADE_REFUND_SUCCESS.name()
        );
        if (!availableRefundTradeStatus.contains(orderInfo.getOrderStatus())) {
            throw new IllegalArgumentException("订单状态不正确，无法退款");
        }

        // 查看退款记录,计算历史退款金额
        LambdaQueryWrapper<OrderRefundInfo> refundQueryWrapper = new LambdaQueryWrapper<>();
        refundQueryWrapper.eq(OrderRefundInfo::getOrderId, orderId);
        refundQueryWrapper.eq(OrderRefundInfo::getStatus, OrderRefundInfoStatusEnum.TRADE_REFUND_SUCCESS.name());
        List<OrderRefundInfo> existingRefundList = orderRefundInfoRepository.selectList(refundQueryWrapper);
        long totalRefundedAmount = existingRefundList.stream()
                .mapToLong(OrderRefundInfo::getOrderRefundAmount)
                .sum();
        if (totalRefundedAmount + orderRefundAmount > orderInfo.getOrderAmount()) {
            throw new IllegalArgumentException("退款金额超过订单总金额");
        }
        if (orderRefundAmount <= 0) {
            throw new IllegalArgumentException("退款金额必须大于0");
        }

        // 创建退款记录
        OrderRefundInfo orderRefundInfo = new OrderRefundInfo();
        String refundOrderId = OrderInfoUtils.generateOrderId(orderInfo.getUserId());
        orderRefundInfo.setRefundOrderId(refundOrderId);
        orderRefundInfo.setOutBizNo(refundOrderId);
        orderRefundInfo.setOrderId(orderId);
        orderRefundInfo.setOrderPayChannel(orderPaymentInfo.getOrderPayChannel());
        orderRefundInfo.setOrderRefundTime(new Date());
        orderRefundInfo.setOrderRefundAmount(orderRefundAmount);
        orderRefundInfo.setRefundReason(refundReason);
        orderRefundInfo.setStatus(OrderRefundInfoStatusEnum.TRADE_REFUND_PENDING.name());
        if (returnPurchase == null) {
            returnPurchase = false;
        }
        orderRefundInfo.setReturnPurchase(returnPurchase);


        // 请求退款接口
        PaymentChannelEnum paymentChannelEnum = PaymentChannelEnum.valueOf(orderPaymentInfo.getOrderPayChannel());
        PaymentService paymentService = PaymentServiceFactory.getPaymentService(paymentChannelEnum);
        paymentService.createRefundOrder(orderRefundInfo);

        // 保存退款记录
        orderRefundInfoRepository.insert(orderRefundInfo);

        return refundOrderId;
    }

//    private void changeOrderStatusToRefunded(OrderInfo orderInfo, OrderRefundInfo orderRefundInfo, Boolean premiumPlanReturned) {
//        LambdaUpdateWrapper<OrderInfo> orderInfoUpdate = new LambdaUpdateWrapper<>();
//
//        if (orderRefundInfo.getOrderRefundAmount().equals(orderInfo.getOrderAmount())) {
//            orderInfoUpdate.set(OrderInfo::getOrderStatus, OrderInfoStatusEnum.TRADE_CLOSED.name());
//            orderInfoRepository.update(orderInfoUpdate);
//            return;
//        }
//
//        LambdaQueryWrapper<OrderRefundInfo> refundQueryWrapper = new LambdaQueryWrapper<>();
//        refundQueryWrapper.eq(OrderRefundInfo::getOrderId, orderInfo.getOrderId());
//        refundQueryWrapper.eq(OrderRefundInfo::getStatus, OrderInfoStatusEnum.TRADE_REFUND_SUCCESS.name());
//        List<OrderRefundInfo> existingRefundList = orderRefundInfoRepository.selectList(refundQueryWrapper);
//        long totalRefundedAmount = existingRefundList.stream()
//                .mapToLong(OrderRefundInfo::getOrderRefundAmount)
//                .sum();
//        if (totalRefundedAmount == orderInfo.getOrderAmount()) {
//            orderInfoUpdate.set(OrderInfo::getOrderStatus, OrderInfoStatusEnum.TRADE_CLOSED.name());
//        } else {
//            orderInfoUpdate.set(OrderInfo::getOrderStatus, OrderInfoStatusEnum.TRADE_REFUND_SUCCESS.name());
//        }
//
//        if (premiumPlanReturned) {
//            orderInfoUpdate.set(OrderInfo::getPremiumPlanReturned, true);
//        }
//
//
//        orderInfoRepository.update(orderInfoUpdate);
//    }

}
