package org.nginx.auth.model;

import java.util.Date;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
public class OrderPaymentInfo extends AutoIncrementBaseEntity {

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
     * 支付流水号
     */
    private String payNo;
    /**
     * 支付时间
     */
    private Date orderPayTime;
    /**
     * 实付金额,单位分
     */
    private Long orderPayAmount;
    /**
     * 0 已关闭
     */
    private Boolean inUse;
    /**
     * 订单状态
     */
    private String status;

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

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public Date getOrderPayTime() {
        return orderPayTime;
    }

    public void setOrderPayTime(Date orderPayTime) {
        this.orderPayTime = orderPayTime;
    }

    public Long getOrderPayAmount() {
        return orderPayAmount;
    }

    public void setOrderPayAmount(Long orderPayAmount) {
        this.orderPayAmount = orderPayAmount;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
