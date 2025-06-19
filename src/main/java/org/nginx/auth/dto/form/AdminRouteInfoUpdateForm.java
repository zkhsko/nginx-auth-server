package org.nginx.auth.dto.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author dongpo.li
 * @date 2025/1/4 11:50
 */
public class AdminRouteInfoUpdateForm {

    private Long id;

    @NotBlank(message = "生效条件列表不能为空")
    private String routePredicate;

    @NotBlank(message = "过滤器列表不能为空")
    private String routeFilter;

    @NotBlank(message = "服务地址不能为空")
    private String routeUri;

    @NotNull(message = "生效优先级不能为空")
    private Integer routeOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoutePredicate() {
        return routePredicate;
    }

    public void setRoutePredicate(String routePredicate) {
        this.routePredicate = routePredicate;
    }

    public String getRouteFilter() {
        return routeFilter;
    }

    public void setRouteFilter(String routeFilter) {
        this.routeFilter = routeFilter;
    }

    public String getRouteUri() {
        return routeUri;
    }

    public void setRouteUri(String routeUri) {
        this.routeUri = routeUri;
    }

    public Integer getRouteOrder() {
        return routeOrder;
    }

    public void setRouteOrder(Integer routeOrder) {
        this.routeOrder = routeOrder;
    }
}
