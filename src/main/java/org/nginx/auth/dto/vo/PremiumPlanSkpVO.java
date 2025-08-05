package org.nginx.auth.dto.vo;

import org.nginx.auth.model.PremiumPlanSkp;
import org.nginx.auth.model.PremiumPlanSku;

import java.util.List;

public class PremiumPlanSkpVO {

    private PremiumPlanSkp premiumPlanSkp;

    private List<PremiumPlanSkuVO> premiumPlanSkuList;

    public PremiumPlanSkp getPremiumPlanSkp() {
        return premiumPlanSkp;
    }

    public void setPremiumPlanSkp(PremiumPlanSkp premiumPlanSkp) {
        this.premiumPlanSkp = premiumPlanSkp;
    }

    public List<PremiumPlanSkuVO> getPremiumPlanSkuList() {
        return premiumPlanSkuList;
    }

    public void setPremiumPlanSkuList(List<PremiumPlanSkuVO> premiumPlanSkuList) {
        this.premiumPlanSkuList = premiumPlanSkuList;
    }
}
