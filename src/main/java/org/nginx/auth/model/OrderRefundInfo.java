package org.nginx.auth.model;

import java.util.Date;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
public class OrderRefundInfo extends AutoIncrementBaseEntity {

    /**
     * 退款单唯一编号
     */
    private String refundOrderId;
    /**
     * 订单唯一编号
     */
    private String orderId;
    /**
     * 支付渠道
     *
     */
    private String orderPayChannel;
    /**
     * 支付时间
     */
    private Date orderRefundTime;
    /**
     * 实付金额,单位分
     */
    private Long orderRefundAmount;
    /**
     * 退款单状态
     */
    private String status;
    private String refundReason;
    /**
     * 是否退货
     */
    private boolean returnPurchase;

    public String getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderPayChannel() {
        return orderPayChannel;
    }

    public void setOrderPayChannel(String orderPayChannel) {
        this.orderPayChannel = orderPayChannel;
    }

    public Date getOrderRefundTime() {
        return orderRefundTime;
    }

    public void setOrderRefundTime(Date orderRefundTime) {
        this.orderRefundTime = orderRefundTime;
    }

    public Long getOrderRefundAmount() {
        return orderRefundAmount;
    }

    public void setOrderRefundAmount(Long orderRefundAmount) {
        this.orderRefundAmount = orderRefundAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public boolean isReturnPurchase() {
        return returnPurchase;
    }

    public void setReturnPurchase(boolean returnPurchase) {
        this.returnPurchase = returnPurchase;
    }
}
