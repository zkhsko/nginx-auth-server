package org.nginx.auth.service.payment;

import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.OrderRefundInfo;
import org.nginx.auth.model.PaymentNotifyHistory;
import org.nginx.auth.response.OrderCreateDTO;

import java.util.Map;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
public interface PaymentService {

    /**
     * 1. 从哪里取
     * 2. 怎么解析
     *
     * @param paymentNotifyHistory
     * @return
     */
    Map<String, String> resolveRequestParam(PaymentNotifyHistory paymentNotifyHistory);

    void handleNotifyAction(Map<String, String> requestParamMap);

    OrderCreateDTO createOrder(OrderInfo orderInfo);

    OrderRefundInfo createRefundOrder(OrderRefundInfo orderRefundInfo);

    void onPaymentSuccess(OrderPaymentInfo orderPaymentInfo);

    void onRefundSuccess(OrderRefundInfo orderRefundInfo);

    OrderRefundInfo queryRefundInfo(String orderId, String outBizNo);

}
