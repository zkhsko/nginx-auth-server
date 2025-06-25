package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.EnumUtils;
import org.nginx.auth.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.request.OrderCreateParam;
import org.nginx.auth.response.OrderCreateDTO;
import org.nginx.auth.service.OrderInfoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @RequestMapping(value = "/order/alipay/notify")
    @ResponseBody
    public String orderAlipayNotify(HttpServletRequest request, HttpServletResponse response,
                                    @RequestBody String requestBody) {

        Map<String, String[]> requestParam = request.getParameterMap();

        logger.info("支付宝支付成功回调通知, queryParam={}, requestBody={}", JsonUtils.toJson(requestParam), requestBody);
        return "success";

    }

}
