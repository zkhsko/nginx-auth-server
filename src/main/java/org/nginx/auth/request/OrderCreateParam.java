package org.nginx.auth.request;

/**
 * @author dongpo.li
 * @date 2023/12/22
 */
public class OrderCreateParam {

    private String productId;
    private String paymentChannel;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }
}
