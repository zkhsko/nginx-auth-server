package org.nginx.auth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author dongpo.li
 * @date 2024/08/01
 */
@TableName("premium_plan_sku")
public class PremiumPlanSku extends AutoIncrementBaseEntity {

    /**
     * premium_plan_skp.id
     */
    @TableField("premium_plan_skp_id")
    private Long premiumPlanSkpId;
    /**
     * 商品sku的展示名称 如 月，季度，半年，年等
     */
    @TableField("premium_plan_sku_name")
    private String premiumPlanSkuName;
    /**
     * 商品价格，单位分
     */
    @TableField("premium_plan_price")
    private Long premiumPlanPrice;
    /**
     * 商品有效期单位，可选值：DAY、MONTH、YEAR
     */
    @TableField("premium_plan_time_unit")
    private String premiumPlanTimeUnit;
    /**
     * 商品有效期数值，与单位一起计算过期时间
     */
    @TableField("premium_plan_time_value")
    private Long premiumPlanTimeValue;
    /**
     * 库存
     */
    @TableField("premium_plan_stock")
    private Integer premiumPlanStock;
    /**
     * 是否上架中，上架中的商品才能下单
     */
    @TableField("in_use")
    private Boolean inUse;

    public Long getPremiumPlanSkpId() {
        return premiumPlanSkpId;
    }

    public void setPremiumPlanSkpId(Long premiumPlanSkpId) {
        this.premiumPlanSkpId = premiumPlanSkpId;
    }

    public String getPremiumPlanSkuName() {
        return premiumPlanSkuName;
    }

    public void setPremiumPlanSkuName(String premiumPlanSkuName) {
        this.premiumPlanSkuName = premiumPlanSkuName;
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

    public Integer getPremiumPlanStock() {
        return premiumPlanStock;
    }

    public void setPremiumPlanStock(Integer premiumPlanStock) {
        this.premiumPlanStock = premiumPlanStock;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }
}