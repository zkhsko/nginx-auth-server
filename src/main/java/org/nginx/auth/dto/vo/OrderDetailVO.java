package org.nginx.auth.dto.vo;

import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.OrderRefundInfo;
import org.nginx.auth.model.OrderSkuInfo;
import org.nginx.auth.model.RefundSupport;

import java.util.List;

public class OrderDetailVO {

    private OrderInfo orderInfo;
    private List<OrderSkuInfo> orderSkuInfoList;
    private List<OrderPaymentInfo> paymentList;
    private List<OrderRefundInfo> refundHistory;
    private List<RefundSupport> refundSupportList;

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

    public List<OrderPaymentInfo> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<OrderPaymentInfo> paymentList) {
        this.paymentList = paymentList;
    }

    public List<OrderRefundInfo> getRefundHistory() {
        return refundHistory;
    }

    public void setRefundHistory(List<OrderRefundInfo> refundHistory) {
        this.refundHistory = refundHistory;
    }

    public List<RefundSupport> getRefundSupportList() {
        return refundSupportList;
    }

    public void setRefundSupportList(List<RefundSupport> refundSupportList) {
        this.refundSupportList = refundSupportList;
    }
}
