package org.nginx.auth.model;

public class PaymentNotifyHistory extends AutoIncrementBaseEntity {

    /**
     * 唯一编号
     */
    private String notifyId;
    /**
     * 支付渠道
     */
    private String orderPayChannel;
    /**
     * 请求参数
     */
    private String requestParam;
    /**
     * 请求体
     */
    private String requestBody;
    /**
     * 是否已处理
     */
    private Boolean resolved;

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getOrderPayChannel() {
        return orderPayChannel;
    }

    public void setOrderPayChannel(String orderPayChannel) {
        this.orderPayChannel = orderPayChannel;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }
}
