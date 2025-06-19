package org.nginx.auth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
@TableName("plan_info")
public class PlanInfo extends AutoIncrementBaseEntity {

    @TableField("plan_name")
    private String planName;
    @TableField("plan_desc")
    private String planDesc;
    @TableField("route_list_text")
    private String routeListText;
    /**
     * 产品价格,单位分
     */
    @TableField("price")
    private Long price;
    /**
     * 产品有效期单位 可选值：
     * DAY: 天,从当前时间到几天后的这个时间
     * MONTH: 月,...
     * YEAR: 年:...
     */
    @TableField("plan_time_unit")
    private String planTimeUnit;
    /**
     * 产品有效期值,和单位一起计算过期时间
     */
    @TableField("plan_time_value")
    private Long planTimeValue;
    /**
     * 库存
     */
    @TableField("stock")
    private Integer stock;
    /**
     * 是否上架中,上架中的商品才能下单
     */
    @TableField("in_use")
    private Boolean inUse;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDesc() {
        return planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }

    public String getRouteListText() {
        return routeListText;
    }

    public void setRouteListText(String routeListText) {
        this.routeListText = routeListText;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getPlanTimeUnit() {
        return planTimeUnit;
    }

    public void setPlanTimeUnit(String planTimeUnit) {
        this.planTimeUnit = planTimeUnit;
    }

    public Long getPlanTimeValue() {
        return planTimeValue;
    }

    public void setPlanTimeValue(Long planTimeValue) {
        this.planTimeValue = planTimeValue;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }
}
