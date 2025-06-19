package org.nginx.auth.service.payment;

import org.nginx.auth.model.PaymentHistory;
import org.nginx.auth.response.OrderCreateDTO;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public interface PaymentService {

    OrderCreateDTO createOrder(PaymentHistory paymentHistory);

    void pay(PaymentHistory paymentHistory);



}
