package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.OrderRefundInfo;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.repository.OrderInfoRepository;
import org.nginx.auth.repository.OrderPaymentInfoRepository;
import org.nginx.auth.repository.OrderRefundInfoRepository;
import org.nginx.auth.service.payment.PaymentService;
import org.nginx.auth.service.payment.PaymentServiceFactory;
import org.nginx.auth.util.OrderInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void refundByAdmin(String orderId, Long orderRefundAmount, String refundReason, Boolean returnPurchase) {
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
        Set<String> availableRefundTradeStatus = Set.of("TRADE_SUCCESS");
        if (!availableRefundTradeStatus.contains(orderPaymentInfo.getStatus())) {
            throw new IllegalArgumentException("订单状态不正确，无法退款");
        }

        // 查看退款记录,计算历史退款金额
        // TODO: 退款失败的不能算
        LambdaQueryWrapper<OrderRefundInfo> refundQueryWrapper = new LambdaQueryWrapper<>();
        refundQueryWrapper.eq(OrderRefundInfo::getOrderId, orderId);
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

        // 请求退款接口
        PaymentChannelEnum paymentChannelEnum = PaymentChannelEnum.valueOf(orderPaymentInfo.getOrderPayChannel());
        PaymentService paymentService = PaymentServiceFactory.getPaymentService(paymentChannelEnum);

        // 创建退款记录
        OrderRefundInfo orderRefundInfo = new OrderRefundInfo();
        String refundOrderId = OrderInfoUtils.generateOrderId(orderInfo.getUserId());
        orderRefundInfo.setRefundOrderId(refundOrderId);
        orderRefundInfo.setOrderId(orderId);
        orderRefundInfo.setOrderPayChannel(orderPaymentInfo.getOrderPayChannel());
        orderRefundInfo.setTradeNo("");
        orderRefundInfo.setOrderRefundTime(new Date());
        orderRefundInfo.setOrderRefundAmount(orderRefundAmount);
        orderRefundInfo.setRefundReason(refundReason);
        orderRefundInfo.setStatus("REFUND_PROCESSING");
        if (returnPurchase == null) {
            returnPurchase = false;
        }
        orderRefundInfo.setReturnPurchase(returnPurchase);

        paymentService.createRefundOrder(orderRefundInfo);

        // 保存退款记录
        orderRefundInfoRepository.insert(orderRefundInfo);

        // TODO: 要不要修改主订单状态
    }

}
