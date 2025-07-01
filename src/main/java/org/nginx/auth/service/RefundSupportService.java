package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.RefundSupport;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.RefundSupportRepository;
import org.nginx.auth.util.OrderInfoUtils;
import org.nginx.auth.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RefundSupportService {

    @Autowired
    private RefundSupportRepository refundSupportRepository;
    @Autowired
    private OrderInfoService orderInfoService;

    public void createRefundSupport(String orderId, Long refundAmount, Boolean refundPurchase, String refundReason) {

        User user = SessionUtil.getCurrentUser();
        if (user == null) {
            return;
        }

        Long userId = user.getId();
        String refundSupportId = OrderInfoUtils.generateOrderId(userId);

        // 检查订单是否存在
        OrderInfo orderInfo = orderInfoService.selectByOrderId(orderId);
        if (orderInfo == null) {
            throw new IllegalArgumentException("Order does not exist: " + orderId);
        }
        if (!Objects.equals(orderInfo.getUserId(), userId)) {
            throw new IllegalArgumentException("Order does not belong to the current user: " + orderId);
        }


        RefundSupport refundSupport = new RefundSupport();
        refundSupport.setRefundSupportId(refundSupportId);
        refundSupport.setUserId(userId);
        refundSupport.setOrderId(orderId);
        refundSupport.setRefundAmount(refundAmount);
        refundSupport.setRefundPurchase(refundPurchase);
        refundSupport.setRefundReason(refundReason);
        refundSupport.setRemarkText("");

        // 保存退款申请
        refundSupportRepository.insert(refundSupport);
    }

}
