package org.nginx.auth.dto.vo;

import org.nginx.auth.model.PremiumPlan;

public class PremiumPlanVO {

    private PremiumPlan premiumPlan;

    private String premiumPlanPriceYuan;

    public PremiumPlan getPremiumPlan() {
        return premiumPlan;
    }

    public void setPremiumPlan(PremiumPlan premiumPlan) {
        this.premiumPlan = premiumPlan;
    }

    public String getPremiumPlanPriceYuan() {
        return premiumPlanPriceYuan;
    }

    public void setPremiumPlanPriceYuan(String premiumPlanPriceYuan) {
        this.premiumPlanPriceYuan = premiumPlanPriceYuan;
    }
}
