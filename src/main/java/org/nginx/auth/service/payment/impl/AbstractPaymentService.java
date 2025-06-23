package org.nginx.auth.service.payment.impl;

import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.repository.PaymentHistoryRepository;
import org.nginx.auth.service.payment.PaymentService;

import java.util.Date;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public abstract class AbstractPaymentService implements PaymentService {

    private PaymentHistoryRepository paymentHistoryRepository;

    public void setPaymentInfoRepository(PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    protected void updateOrderPayInfo(OrderPaymentInfo orderPaymentInfo) {

        OrderPaymentInfo orderPaymentInfoUpdate = new OrderPaymentInfo();
        orderPaymentInfoUpdate.setTradeNo(orderPaymentInfo.getTradeNo());
        orderPaymentInfoUpdate.setOrderPayTime(new Date());
        orderPaymentInfoUpdate.setOrderPayAmount(orderPaymentInfo.getOrderPayAmount());

        paymentHistoryRepository.updateOrderPayInfo(orderPaymentInfoUpdate);

    }

}
