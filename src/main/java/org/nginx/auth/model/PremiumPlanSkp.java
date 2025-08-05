package org.nginx.auth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
@TableName("premium_plan_skp")
public class PremiumPlanSkp extends AutoIncrementBaseEntity {

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
     * TODO 要加注释
     */
    @TableField("predicate_list_text")
    private String predicateListText;
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

    public String getPredicateListText() {
        return predicateListText;
    }

    public void setPredicateListText(String predicateListText) {
        this.predicateListText = predicateListText;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }
}