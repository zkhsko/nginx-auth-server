package org.nginx.auth.model;

/**
 * 退款申请
 */
public class RefundSupport extends AutoIncrementBaseEntity {

    /**
     * 退款申请唯一编号
     */
    private String refundSupportId;
    /**
     * 申请用户id
     */
    private Long userId;
    // 订单号  orderId
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 退款金额，单位分
     */
    private Long refundAmount;
    /**
     * 是否退货
     */
    private Boolean refundPurchase;
    /**
     * 退款原因
     */
    private String refundReason;
    /**
     * 备注
     */
    private String remarkText;
    /**
     * 关联的退款单
     */
    private String refundOrderId;
    /**
     * 退款状态
     */
    private String refundSupportStatus;

    public String getRefundSupportId() {
        return refundSupportId;
    }

    public void setRefundSupportId(String refundSupportId) {
        this.refundSupportId = refundSupportId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Long refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Boolean getRefundPurchase() {
        return refundPurchase;
    }

    public void setRefundPurchase(Boolean refundPurchase) {
        this.refundPurchase = refundPurchase;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getRemarkText() {
        return remarkText;
    }

    public void setRemarkText(String remarkText) {
        this.remarkText = remarkText;
    }

    public String getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public String getRefundSupportStatus() {
        return refundSupportStatus;
    }

    public void setRefundSupportStatus(String refundSupportStatus) {
        this.refundSupportStatus = refundSupportStatus;
    }
}
