package org.nginx.auth.dto.vo;

import org.nginx.auth.model.PremiumPlanSku;

public class PremiumPlanSkuVO {

    private PremiumPlanSku premiumPlanSku;

    private String premiumPlanPriceYuan;

    public PremiumPlanSku getPremiumPlanSku() {
        return premiumPlanSku;
    }

    public void setPremiumPlanSku(PremiumPlanSku premiumPlanSku) {
        this.premiumPlanSku = premiumPlanSku;
    }

    public String getPremiumPlanPriceYuan() {
        return premiumPlanPriceYuan;
    }

    public void setPremiumPlanPriceYuan(String premiumPlanPriceYuan) {
        this.premiumPlanPriceYuan = premiumPlanPriceYuan;
    }
}
