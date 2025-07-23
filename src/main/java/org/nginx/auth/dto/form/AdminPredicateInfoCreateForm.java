package org.nginx.auth.dto.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author dongpo.li
 * @date 2025/1/4 11:50
 */
public class AdminPredicateInfoCreateForm {

    @NotBlank(message = "生效条件列表不能为空")
    private String predicate;

    @NotBlank(message = "过滤器列表不能为空")
    private String filter;

    @NotBlank(message = "服务地址不能为空")
    private String uri;

    @NotNull(message = "生效优先级不能为空")
    private Integer order;

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
