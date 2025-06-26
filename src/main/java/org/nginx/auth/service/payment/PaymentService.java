package org.nginx.auth.service.payment;

import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.PaymentNotifyHistory;
import org.nginx.auth.response.OrderCreateDTO;

import java.util.Map;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public interface PaymentService {

    OrderCreateDTO createOrder(OrderInfo orderInfo);

    /**
     * 1. 从哪里取
     * 2. 怎么解析
     *
     * @param paymentNotifyHistory
     * @return
     */
    Map<String, String> resolveRequestParam(PaymentNotifyHistory paymentNotifyHistory);

    void handleNotify(Map<String, String> requestParamMap);


}
