package org.nginx.auth.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.model.PaymentNotifyHistory;
import org.nginx.auth.service.payment.PaymentNotifyHistoryService;
import org.nginx.auth.service.payment.impl.AlipayPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author dongpo.li
 * @date 2024/12/27 20:17
 */
@Controller
@RequestMapping("/anonymous/alipay")
public class AlipayNotifyController {
    private static final Logger logger = LoggerFactory.getLogger(AlipayNotifyController.class);

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


        String notifyId = "";

        String alipayPublicKey = alipayPaymentService.readAlipayPublicKey();

        boolean signVerified = false;
        try {
            PaymentNotifyHistory paymentNotifyHistory = new PaymentNotifyHistory();
            paymentNotifyHistory.setRequestParam(requestQueryString);
            paymentNotifyHistory.setRequestBody(requestBody);
            Map<String, String> requestParam = alipayPaymentService.resolveRequestParam(paymentNotifyHistory);

            signVerified = AlipaySignature.rsaCheckV1(requestParam, alipayPublicKey, "UTF-8", "RSA2");

            notifyId = requestParam.get("notify_id");

        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }

        if (!signVerified) {
            logger.warn("支付宝支付成功回调通知, 签名验证失败, requestQueryString={}, requestBody={}",
                    requestQueryString, requestBody);
            return "failure";
        }

        paymentNotifyHistoryService.insert(notifyId, PaymentChannelEnum.ALIPAY.name(),
                requestQueryString, requestBody);

        return "success";

    }

}
