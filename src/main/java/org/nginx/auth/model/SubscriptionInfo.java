package org.nginx.auth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * @author dongpo.li
 * @date 2024/12/23 16:17
 */
@TableName("subscription_info")
public class SubscriptionInfo extends AutoIncrementBaseEntity {

    @TableField("account_id")
    private Long accountId;
    /**
     * 哪个商品的
     */
    @TableField("plan_id")
    private Long planId;
    /**
     * 订阅下单时间
     */
    @TableField("subscribe_order_time")
    private Date subscribeOrderTime;
    /**
     * @see PlanInfo#planTimeUnit
     */
    @TableField("plan_time_unit")
    private String planTimeUnit;
    /**
     * @see PlanInfo#planTimeValue
     */
    @TableField("plan_time_value")
    private Long planTimeValue;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Date getSubscribeOrderTime() {
        return subscribeOrderTime;
    }

    public void setSubscribeOrderTime(Date subscribeOrderTime) {
        this.subscribeOrderTime = subscribeOrderTime;
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
}
