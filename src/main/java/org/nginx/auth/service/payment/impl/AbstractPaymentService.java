package org.nginx.auth.service.payment.impl;

import org.nginx.auth.model.PaymentHistory;
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

    protected void updateOrderPayInfo(PaymentHistory paymentHistory) {

        PaymentHistory paymentHistoryUpdate = new PaymentHistory();
        paymentHistoryUpdate.setTradeNo(paymentHistory.getTradeNo());
        paymentHistoryUpdate.setOrderPayTime(new Date());
        paymentHistoryUpdate.setOrderPayAmount(paymentHistory.getOrderPayAmount());

        paymentHistoryRepository.updateOrderPayInfo(paymentHistoryUpdate);

    }

}
