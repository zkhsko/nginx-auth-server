package org.nginx.auth.model;

public class OrderSkuInfo extends AutoIncrementBaseEntity {

    private String orderId;

    /**
     * premium_plan_sku表的id
     */
    private Long premiumPlanSkuId;
    /**
     * premium_plan_skp表的id,冗余一下,应该会经常用到
     */
    private Long premiumPlanSkpId;
    /**
     * 数量
     */
    private Long cnt;


    // --- 以下字段来自product_info表 ---
    private String premiumPlanName;
    private String premiumPlanDesc;
    /**
     * 产品价格,单位分
     */
    private Long premiumPlanPrice;
    /**
     * 产品有效期单位 可选值：
     * DAY: 天,从当前时间到几天后的这个时间
     * MONTH: 月,...
     * YEAR: 年:...
     */
    private String premiumPlanTimeUnit;
    /**
     * 产品有效期值,和单位一起计算过期时间
     */
    private Long premiumPlanTimeValue;
    // --- premium_plan表结束 ---


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getPremiumPlanSkuId() {
        return premiumPlanSkuId;
    }

    public void setPremiumPlanSkuId(Long premiumPlanSkuId) {
        this.premiumPlanSkuId = premiumPlanSkuId;
    }

    public Long getPremiumPlanSkpId() {
        return premiumPlanSkpId;
    }

    public void setPremiumPlanSkpId(Long premiumPlanSkpId) {
        this.premiumPlanSkpId = premiumPlanSkpId;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }

    public String getPremiumPlanName() {
        return premiumPlanName;
    }

    public void setPremiumPlanName(String premiumPlanName) {
        this.premiumPlanName = premiumPlanName;
    }

    public String getPremiumPlanDesc() {
        return premiumPlanDesc;
    }

    public void setPremiumPlanDesc(String premiumPlanDesc) {
        this.premiumPlanDesc = premiumPlanDesc;
    }

    public Long getPremiumPlanPrice() {
        return premiumPlanPrice;
    }

    public void setPremiumPlanPrice(Long premiumPlanPrice) {
        this.premiumPlanPrice = premiumPlanPrice;
    }

    public String getPremiumPlanTimeUnit() {
        return premiumPlanTimeUnit;
    }

    public void setPremiumPlanTimeUnit(String premiumPlanTimeUnit) {
        this.premiumPlanTimeUnit = premiumPlanTimeUnit;
    }

    public Long getPremiumPlanTimeValue() {
        return premiumPlanTimeValue;
    }

    public void setPremiumPlanTimeValue(Long premiumPlanTimeValue) {
        this.premiumPlanTimeValue = premiumPlanTimeValue;
    }
}
