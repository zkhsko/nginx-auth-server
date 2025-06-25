package org.nginx.auth.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.nginx.auth.ApplicationTests;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.request.OrderCreateParam;

/**
 * @author dongpo.li
 * @date 2023/12/20
 */
public class OrderInfoServiceTest extends ApplicationTests {

    @Resource
    private OrderInfoService orderInfoService;

    @Test
    public void testCreateOrder() {
//        OrderCreateParam param = new OrderCreateParam();
//        param.setProductId("1");
//        orderInfoService.createOrder(PaymentChannelEnum.ALIPAY, param);
    }

}
