package org.nginx.auth.model;

import java.util.Date;

public class OrderInfo extends AutoIncrementBaseEntity {

    /**
     * 订单唯一编号
     */
    private String orderId;
    private Long userId;
    private Date orderCreateTime;
    private Long orderAmount;
    private String orderStatus;
    /**
     * 支付流水的id
     */
    private Long orderPaymentInfoId;
    /**
     * 订阅产品已退回
     */
    private Boolean premiumPlanReturned;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(Date orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public Long getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Long orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getOrderPaymentInfoId() {
        return orderPaymentInfoId;
    }

    public void setOrderPaymentInfoId(Long orderPaymentInfoId) {
        this.orderPaymentInfoId = orderPaymentInfoId;
    }

    public Boolean getPremiumPlanReturned() {
        return premiumPlanReturned;
    }

    public void setPremiumPlanReturned(Boolean premiumPlanReturned) {
        this.premiumPlanReturned = premiumPlanReturned;
    }
}
