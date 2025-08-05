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

    public Long getPremiumPlanSkpId() {
        return premiumPlanSkpId;
    }

    public void setPremiumPlanSkpId(Long premiumPlanSkpId) {
        this.premiumPlanSkpId = premiumPlanSkpId;
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
}