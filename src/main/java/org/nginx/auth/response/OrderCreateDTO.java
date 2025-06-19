package org.nginx.auth.response;

/**
 * @author dongpo.li
 * @date 2023/12/22
 */
public class OrderCreateDTO {

    private String orderId;
    private String imageData;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}
