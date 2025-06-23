package org.nginx.auth.request;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2023/12/22
 */
public class OrderCreateParam {

    private String skuId;
    private Long cnt;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }
}
