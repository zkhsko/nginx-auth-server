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
     * 退款请求号。
     * 标识一次退款请求，需要保证在交易号下唯一，如需部分退款，则此参数必传。
     * 注：针对同一次退款请求，如果调用接口失败或异常了，重试时需要保证退款请求号不能变更，防止该笔交易重复退款。
     * 支付宝会保证同样的退款请求号多次请求只会退一次。
     * 管理员发起的退款申请,是可以指定的,所以这个值等于refundOrderId
     * 但是如果是支付宝后台发起的退款,这个值就不等于refundOrderId了
     */
    private String outBizNo;
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
    private Boolean returnPurchase;

    public String getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
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

    public Boolean getReturnPurchase() {
        return returnPurchase;
    }

    public void setReturnPurchase(Boolean returnPurchase) {
        this.returnPurchase = returnPurchase;
    }
}
