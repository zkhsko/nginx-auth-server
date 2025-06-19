package org.nginx.auth.model;

import java.util.Date;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
public class PaymentHistory extends AutoIncrementBaseEntity {

    /**
     * 订单唯一编号
     */
    private String orderId;
    private Long accountId;
    /**
     * product_info表的id
     */
    private Long productId;
    /**
     * 支付渠道
     *
     */
    private String orderPayChannel;
    /**
     * 支付流水号
     */
    private String tradeNo;
    /**
     * 支付时间
     */
    private Date orderPayTime;
    /**
     * 实付金额,单位分
     */
    private Long orderPayAmount;

    // --- 以下字段来自product_info表 ---
    private String productName;
    private String productDesc;
    /**
     * 权限路径表达式,参见AntPathMatcher
     */
    private String pattern;
    /**
     * 产品价格,单位分
     */
    private Long productPrice;
    /**
     * TODO 改名Plan了
     * 产品有效期单位 可选值：
     * DAY: 天,从当前时间到几天后的这个时间
     * MONTH: 月,...
     * YEAR: 年:...
     */
    private String productTimeUnit;
    /**
     * 产品有效期值,和单位一起计算过期时间
     */
    private Long productTimeValue;
    // --- product_info表结束 ---

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getOrderPayChannel() {
        return orderPayChannel;
    }

    public void setOrderPayChannel(String orderPayChannel) {
        this.orderPayChannel = orderPayChannel;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Long productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductTimeUnit() {
        return productTimeUnit;
    }

    public void setProductTimeUnit(String productTimeUnit) {
        this.productTimeUnit = productTimeUnit;
    }

    public Long getProductTimeValue() {
        return productTimeValue;
    }

    public void setProductTimeValue(Long productTimeValue) {
        this.productTimeValue = productTimeValue;
    }

}
