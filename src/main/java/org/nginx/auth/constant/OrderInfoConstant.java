package org.nginx.auth.constant;

import org.nginx.auth.enums.OrderInfoStatusEnum;

import java.util.Set;

public class OrderInfoConstant {

    public static final Set<String> AVAILABLE_REFUND_TRADE_STATUS = Set.of(
            OrderInfoStatusEnum.PAYMENT_SUCCESS.name(),
            OrderInfoStatusEnum.TRADE_REFUND_SUCCESS.name()
    );

}
