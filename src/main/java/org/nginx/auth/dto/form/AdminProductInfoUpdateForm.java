package org.nginx.auth.dto.form;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

/**
 * @author dongpo.li
 * @date 2025/1/6 16:54
 */
public class AdminProductInfoUpdateForm {

    private Long id;
    @NotBlank(message = "产品名称不能为空")
    private String productName;
    //    @NotBlank(message = "产品描述不能为空")
    private String productDesc;
    @NotNull(message = "请至少选择关联一个路由规则")
    @NotEmpty(message = "请至少选择关联一个路由规则")
    private LinkedHashSet<Long> routeList = new LinkedHashSet<>();
    @NotNull(message = "产品价格不能为空")
    @Digits(integer = 9, fraction = 2, message = "产品价格不合法")
    @Positive(message = "产品价格必须为正数")
    private String productPrice;
    @NotBlank(message = "产品有效期单位不能为空")
    @Pattern(regexp = "DAY|MONTH|YEAR", message = "产品有效期单位不合法")
    private String productTimeUnit;
    /**
     * 产品有效期值,和单位一起计算过期时间
     */
    @NotNull(message = "产品有效期值不能为空")
    @Positive(message = "产品有效期必须为正数")
    @Digits(integer = 9, fraction = 0, message = "产品有效期值不合法")
    private String productTimeValue;
    /**
     * 库存
     */
    @NotNull(message = "产品库存不能为空")
    @PositiveOrZero(message = "产品库存不能为负数")
    @Digits(integer = 9, fraction = 0, message = "产品库存不合法")
    private String productStock;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public LinkedHashSet<Long> getRouteList() {
        return routeList;
    }

    public void setRouteList(LinkedHashSet<Long> routeList) {
        this.routeList = routeList;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductTimeUnit() {
        return productTimeUnit;
    }

    public void setProductTimeUnit(String productTimeUnit) {
        this.productTimeUnit = productTimeUnit;
    }

    public String getProductTimeValue() {
        return productTimeValue;
    }

    public void setProductTimeValue(String productTimeValue) {
        this.productTimeValue = productTimeValue;
    }

    public String getProductStock() {
        return productStock;
    }

    public void setProductStock(String productStock) {
        this.productStock = productStock;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }
}
