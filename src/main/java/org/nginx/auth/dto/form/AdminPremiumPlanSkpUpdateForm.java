package org.nginx.auth.dto.form;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

/**
 * @author dongpo.li
 * @date 2025/1/6 16:54
 */
public class AdminPremiumPlanSkpUpdateForm {

    private Long id;
    @NotBlank(message = "高级商品名称不能为空")
    private String premiumPlanName;
    private String premiumPlanDesc;
    // TODO 现在叫断言,但还是不准确,等再想个名字再改吧
    @NotNull(message = "请至少选择关联一个路由规则")
    @NotEmpty(message = "请至少选择关联一个路由规则")
    private LinkedHashSet<Long> predicateList = new LinkedHashSet<>();
    /**
     * 是否上架中,上架中的商品才能下单
     */
    @NotNull(message = "是否上架不能为空")
    private Boolean inUse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LinkedHashSet<Long> getPredicateList() {
        return predicateList;
    }

    public void setPredicateList(LinkedHashSet<Long> predicateList) {
        this.predicateList = predicateList;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }
}
