package org.nginx.auth.dto.vo;

import org.nginx.auth.model.PremiumPlanSkp;

/**
 * 带SKU状态的商品信息VO
 * @author dongpo.li
 * @date 2025/8/7
 */
public class AdminPremiumPlanSkpPageDataVO {

    private PremiumPlanSkp premiumPlanSkp;
    
    /**
     * 是否有上架中的SKU
     */
    private Boolean hasActiveSku;

    public PremiumPlanSkp getPremiumPlanSkp() {
        return premiumPlanSkp;
    }

    public void setPremiumPlanSkp(PremiumPlanSkp premiumPlanSkp) {
        this.premiumPlanSkp = premiumPlanSkp;
    }

    public Boolean getHasActiveSku() {
        return hasActiveSku;
    }

    public void setHasActiveSku(Boolean hasActiveSku) {
        this.hasActiveSku = hasActiveSku;
    }
}