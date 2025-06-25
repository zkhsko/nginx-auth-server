package org.nginx.auth.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.service.payment.PaymentNotifyHistoryService;
import org.nginx.auth.service.payment.impl.AlipayPaymentService;
import org.nginx.auth.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dongpo.li
 * @date 2024/12/27 20:17
 */
@Controller
@RequestMapping("/alipay")
public class AlipayController {
    private static final Logger logger = LoggerFactory.getLogger(AlipayController.class);

    @Autowired
    private AlipayPaymentService alipayPaymentService;
    @Autowired
    private PaymentNotifyHistoryService paymentNotifyHistoryService;

    @RequestMapping(value = "/notify")
    @ResponseBody
    public String alipayNotify(HttpServletRequest request, HttpServletResponse response) {

        String requestQueryString = request.getQueryString();


        String requestBody = "";
        try {
            requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("读取支付宝通知请求体失败", e);
            throw new RuntimeException(e);
        }

        logger.info("支付宝支付成功回调通知, requestQueryString={}, requestBody={}", requestQueryString, requestBody);


        String alipayPublicKey = alipayPaymentService.readAlipayPublicKey();

        boolean signVerified = false;
        try {
            Map<String, String[]> requestParam = request.getParameterMap();
            Map<String, String> singleValueMap = requestParam.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && entry.getValue().length > 0)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue()[0]
                    ));
            signVerified = AlipaySignature.rsaCheckV1(singleValueMap, alipayPublicKey, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        if (!signVerified) {
            logger.warn("支付宝支付成功回调通知, 签名验证失败, requestQueryString={}, requestBody={}",
                    requestQueryString, requestBody);
            return "failure";
        }

        String notifyId = request.getParameter("notify_id");

        paymentNotifyHistoryService.insert(notifyId, PaymentChannelEnum.ALIPAY.name(),
                requestQueryString, requestBody);

        return "success";

    }

}
