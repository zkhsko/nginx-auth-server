package org.nginx.auth.service.payment.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderSkuInfo;
import org.nginx.auth.repository.OrderSkuInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.response.OrderCreateDTO;
import org.nginx.auth.util.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
@Service
public class AlipayPaymentService extends AbstractPaymentService {

    @Value("${payment.alipay.app-id:}")
    private String appId;
    @Value("${payment.alipay.private-key:}")
    private String privateKeyFile;
    @Value("${payment.alipay.alipay-public-key:}")
    private String alipayPublicKeyFile;
    @Value(value = "${payment.alipay.notify-url:}")
    private String notifyUrl;

    @Autowired
    private OrderSkuInfoRepository orderSkuInfoRepository;

    @Override
    public OrderCreateDTO createOrder(OrderInfo orderInfo) {

        String orderId = orderInfo.getOrderId();
        Long orderAmount = orderInfo.getOrderAmount();

        String privateKey = readPrivateKey();
        String alipayPublicKey = readAlipayPublicKey();

        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "UTF-8", alipayPublicKey, "RSA2");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(buildNotifyUrl());
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", orderId);
        BigDecimal totalPayAmount = BigDecimal.valueOf(orderAmount)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
        bizContent.put("total_amount", totalPayAmount.doubleValue());

        LambdaQueryWrapper<OrderSkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderSkuInfo::getOrderId, orderId);
        List<OrderSkuInfo> orderSkuInfoList = orderSkuInfoRepository.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(orderSkuInfoList)) {
            bizContent.put("subject", orderInfo.getOrderId());
        } else if (orderSkuInfoList.size() == 1) {
            bizContent.put("subject", orderSkuInfoList.get(0).getPremiumPlanName());
        } else {
            bizContent.put("subject", orderSkuInfoList.get(0).getPremiumPlanName() + "等" + orderSkuInfoList.size() + "件商品");
        }

        request.setBizContent(JsonUtils.toJson(bizContent));
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);

            System.out.println(JsonUtils.toJson(response));
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }

        String qrCode = response.getQrCode();

        String imageData = translateQRCodeStringToBase64Image(qrCode);

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setOrderId(orderInfo.getOrderId());
        orderCreateDTO.setImageData(imageData);


        return orderCreateDTO;
    }


    private String buildNotifyUrl() {
        return notifyUrl + "/harbor/order/alipay/notify";
    }

    private String readPrivateKey() {
        return readFileContent(privateKeyFile);
    }

    private String readAlipayPublicKey() {
        return readFileContent(alipayPublicKeyFile);
    }

    private String readFileContent(String filePath) {
        try {
            URL url = ResourceUtils.getURL(filePath);
            return IOUtils.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String translateQRCodeStringToBase64Image(String qrCode) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);

            BitMatrix bitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, 250, 250, hints);


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", out);

            byte[] bytes = out.toByteArray();

            byte[] encode = Base64.getEncoder().encode(bytes);

//            System.out.println(new String(encode));

            return new String(encode);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pay(OrderPaymentInfo orderPaymentInfo) {
        orderPaymentInfo.setOrderPayAmount(1000L);
        orderPaymentInfo.setPayNo(StringUtils.remove(UUID.randomUUID().toString(), '-'));

        updateOrderPayInfo(orderPaymentInfo);
    }
}
