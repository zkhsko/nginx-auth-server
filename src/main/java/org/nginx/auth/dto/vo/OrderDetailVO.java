package org.nginx.auth.dto.vo;

import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderSkuInfo;

import java.util.List;

public class OrderDetailVO {

    private OrderInfo orderInfo;
    private List<OrderSkuInfo> orderSkuInfoList;

    /**
     * 订单状态展示文案
     */
    private String orderStatusDesc;

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public List<OrderSkuInfo> getOrderSkuInfoList() {
        return orderSkuInfoList;
    }

    public void setOrderSkuInfoList(List<OrderSkuInfo> orderSkuInfoList) {
        this.orderSkuInfoList = orderSkuInfoList;
    }

    public String getOrderStatusDesc() {
        return orderStatusDesc;
    }

    public void setOrderStatusDesc(String orderStatusDesc) {
        this.orderStatusDesc = orderStatusDesc;
    }
}
