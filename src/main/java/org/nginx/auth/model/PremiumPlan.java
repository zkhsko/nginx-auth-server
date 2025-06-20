package org.nginx.auth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
@TableName("premium_plan")
public class PremiumPlan extends AutoIncrementBaseEntity {

    /**
     * 高级商品名称
     */
    @TableField("premium_plan_name")
    private String premiumPlanName;
    /**
     * 高级商品描述
     */
    @TableField("premium_plan_desc")
    private String premiumPlanDesc;
    /**
     * 路由列表（JSON文本）
     */
    @TableField("route_list_text")
    private String routeListText;
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

    public String getRouteListText() {
        return routeListText;
    }

    public void setRouteListText(String routeListText) {
        this.routeListText = routeListText;
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
