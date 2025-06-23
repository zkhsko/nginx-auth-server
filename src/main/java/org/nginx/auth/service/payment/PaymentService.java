package org.nginx.auth.service.payment;

import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.response.OrderCreateDTO;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public interface PaymentService {

    OrderCreateDTO createOrder(OrderPaymentInfo orderPaymentInfo);

    void pay(OrderPaymentInfo orderPaymentInfo);



}
