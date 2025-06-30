package org.nginx.auth.enums;

/**
 * 订单状态枚举
 */
public enum OrderInfoStatusEnum {

    /**
     * 待支付
     */
    PENDING_PAYMENT,

    /**
     * 支付成功
     */
    PAYMENT_SUCCESS,

    /**
     * 支付失败
     */
    PAYMENT_FAILED,

    /**
     * 订单已取消
     */
    TRADE_CANCELLED,

    /**
     * 订单已完成,不可再退款
     */
    TRADE_FINISHED,

    /**
     * 交易关闭,不可退款
     */
    TRADE_CLOSED,

    /**
     * 退款完成,一般是部分退款,全部退款后交易关闭
     */
    TRADE_REFUND_SUCCESS,

    ;

}
