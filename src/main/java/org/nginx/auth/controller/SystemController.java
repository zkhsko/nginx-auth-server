package org.nginx.auth.controller;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.request.OrderCreateParam;
import org.nginx.auth.response.OrderCreateDTO;
import org.nginx.auth.service.OrderInfoService;

/**
 * @author dongpo.li
 * @date 2024/12/27 20:17
 */
@Controller
@RequestMapping
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/order/create")
    @ResponseBody
    public OrderCreateDTO createOrder(@RequestBody OrderCreateParam param) {

        String paymentChannel = param.getPaymentChannel();
        boolean validEnum = EnumUtils.isValidEnum(PaymentChannelEnum.class, paymentChannel);
        if (!validEnum) {
            throw new IllegalArgumentException("支付渠道不合法");
        }
        PaymentChannelEnum paymentChannelEnum = EnumUtils.getEnum(PaymentChannelEnum.class, paymentChannel);

        return orderInfoService.createOrder(paymentChannelEnum, param);
    }


//    @RequestMapping(value = "/order/alipay/notify")
//    @ResponseBody
//    public Flux<String> orderAlipayNotify(ServerWebExchange exchange) {
//
//        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
//
//        Flux<DataBuffer> body = exchange.getRequest().getBody();
//
//        return body.map(buffer -> {
//
//            try {
//                InputStream inputStream = buffer.asInputStream();
//                String requestBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//                logger.info("支付宝支付成功回调通知, queryParam={}, requestBody={}", JsonUtils.toJson(queryParams), requestBody);
//                return "success";
//            } catch (Exception e) {
//                logger.error("支付宝支付成功回调异常, queryParam={}", JsonUtils.toJson(queryParams), e);
//                return "fail";
//            }
//
//        });
//
//    }

}
