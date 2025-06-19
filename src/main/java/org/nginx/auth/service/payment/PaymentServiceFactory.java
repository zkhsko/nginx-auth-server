package org.nginx.auth.service.payment;

import org.springframework.stereotype.Component;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.service.payment.impl.AlipayPaymentService;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2025/1/10 19:46
 */
@Component
public class PaymentServiceFactory {

    private static List<PaymentService> paymentServiceList;

    public PaymentServiceFactory(List<PaymentService> paymentServiceList) {
        PaymentServiceFactory.paymentServiceList = paymentServiceList;
    }

    public static PaymentService getPaymentService(PaymentChannelEnum paymentChannelEnum) {

        if (paymentChannelEnum == null) {
            throw new IllegalArgumentException("支付渠道不合法");
        }

        if (paymentServiceList == null || paymentServiceList.isEmpty()) {
            throw new RuntimeException("没有支持的支付方式");
        }

        Class<? extends PaymentService> paymentServiceClass = null;

        switch (paymentChannelEnum) {
            case ALIPAY:
                paymentServiceClass = AlipayPaymentService.class;
                break;
            case WECHAT_PAY:
                return paymentServiceList.get(1);
        }

        for (PaymentService paymentService : paymentServiceList) {
            if (paymentServiceClass.isInstance(paymentService)) {
                return paymentService;
            }
        }

        throw new RuntimeException("不支持的支付方式" + paymentChannelEnum);
    }

}
