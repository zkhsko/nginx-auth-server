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

    @TableField("user_id")
    private Long userId;
    /**
     * premium_plan.id
     * 订阅的哪个产品,跟userId联合唯一
     */
    private Long premiumPlanId;
    /**
     * 订阅到期时间
     */
    @TableField("subscribe_expire_time")
    private Date subscribeExpireTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPremiumPlanId() {
        return premiumPlanId;
    }

    public void setPremiumPlanId(Long premiumPlanId) {
        this.premiumPlanId = premiumPlanId;
    }

    public Date getSubscribeExpireTime() {
        return subscribeExpireTime;
    }

    public void setSubscribeExpireTime(Date subscribeExpireTime) {
        this.subscribeExpireTime = subscribeExpireTime;
    }
}
