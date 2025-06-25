package org.nginx.auth.service.payment;

import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.response.OrderCreateDTO;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public interface PaymentService {

    OrderCreateDTO createOrder(OrderInfo orderInfo);

    void pay(OrderPaymentInfo orderPaymentInfo);



}
