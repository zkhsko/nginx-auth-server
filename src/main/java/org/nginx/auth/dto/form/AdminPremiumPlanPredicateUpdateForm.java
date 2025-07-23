package org.nginx.auth.dto.form;


import jakarta.validation.constraints.NotBlank;

/**
 * @author dongpo.li
 * @date 2025/1/4 11:50
 */
public class AdminPremiumPlanPredicateUpdateForm {

    private Long id;

    @NotBlank(message = "断言名称不能为空")
    private String predicateName;

    @NotBlank(message = "断言内容不能为空")
    private String predicateText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public void setPredicateName(String predicateName) {
        this.predicateName = predicateName;
    }

    public String getPredicateText() {
        return predicateText;
    }

    public void setPredicateText(String predicateText) {
        this.predicateText = predicateText;
    }
}
