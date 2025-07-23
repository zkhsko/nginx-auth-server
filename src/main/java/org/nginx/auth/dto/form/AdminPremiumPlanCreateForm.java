package org.nginx.auth.dto.form;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

/**
 * @author dongpo.li
 * @date 2025/1/6 16:54
 */
public class AdminPremiumPlanCreateForm {

    @NotBlank(message = "高级商品名称不能为空")
    private String premiumPlanName;
    private String premiumPlanDesc;
//    @NotNull(message = "请至少选择关联一个路由规则")
//    @NotEmpty(message = "请至少选择关联一个路由规则")
    private LinkedHashSet<Long> predicateList = new LinkedHashSet<>();
    @NotNull(message = "商品价格不能为空")
    @Digits(integer = 9, fraction = 2, message = "商品价格不合法")
    @Positive(message = "商品价格必须为正数")
    private BigDecimal premiumPlanPrice;
    @NotBlank(message = "商品有效期单位不能为空")
    @Pattern(regexp = "DAY|MONTH|YEAR", message = "商品有效期单位不合法")
    private String premiumPlanTimeUnit;
    /**
     * 商品有效期值,和单位一起计算过期时间
     */
    @NotNull(message = "商品有效期值不能为空")
    @Positive(message = "商品有效期必须为正数")
    @Digits(integer = 9, fraction = 0, message = "商品有效期值不合法")
    private Long premiumPlanTimeValue;
    /**
     * 库存
     */
    @NotNull(message = "商品库存不能为空")
    @PositiveOrZero(message = "商品库存不能为负数")
    @Digits(integer = 9, fraction = 0, message = "商品库存不合法")
    private Integer premiumPlanStock;
    /**
     * 是否上架中,上架中的商品才能下单
     */
    @NotNull(message = "是否上架不能为空")
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

    public LinkedHashSet<Long> getPredicateList() {
        return predicateList;
    }

    public void setPredicateList(LinkedHashSet<Long> predicateList) {
        this.predicateList = predicateList;
    }

    public BigDecimal getPremiumPlanPrice() {
        return premiumPlanPrice;
    }

    public void setPremiumPlanPrice(BigDecimal premiumPlanPrice) {
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
