package org.nginx.auth.service.payment.impl;

import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.repository.OrderPaymentInfoRepository;
import org.nginx.auth.service.payment.PaymentService;

import java.util.Date;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public abstract class AbstractPaymentService implements PaymentService {

    private OrderPaymentInfoRepository orderPaymentInfoRepository;

    public void setPaymentInfoRepository(OrderPaymentInfoRepository orderPaymentInfoRepository) {
        this.orderPaymentInfoRepository = orderPaymentInfoRepository;
    }

    protected void updateOrderPayInfo(OrderPaymentInfo orderPaymentInfo) {

        OrderPaymentInfo orderPaymentInfoUpdate = new OrderPaymentInfo();
        orderPaymentInfoUpdate.setPayNo(orderPaymentInfo.getPayNo());
        orderPaymentInfoUpdate.setOrderPayTime(new Date());
        orderPaymentInfoUpdate.setOrderPayAmount(orderPaymentInfo.getOrderPayAmount());

        orderPaymentInfoRepository.updateOrderPayInfo(orderPaymentInfoUpdate);

    }

}
