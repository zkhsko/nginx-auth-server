package org.nginx.auth.service.payment.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.model.*;
import org.nginx.auth.repository.OrderInfoRepository;
import org.nginx.auth.repository.OrderPaymentInfoRepository;
import org.nginx.auth.repository.OrderSkuInfoRepository;
import org.nginx.auth.response.OrderCreateDTO;
import org.nginx.auth.service.SubscriptionInfoService;
import org.nginx.auth.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author dongpo.li
 * @date 2023/12/21
 */
@Service
public class AlipayPaymentService extends AbstractPaymentService {
    private static final Logger logger = LoggerFactory.getLogger(AlipayPaymentService.class);

    @Value("${payment.alipay.app-id:}")
    private String appId;
    @Value("${payment.alipay.private-key:}")
    private String privateKeyFile;
    @Value("${payment.alipay.alipay-public-key:}")
    private String alipayPublicKeyFile;
    @Value(value = "${payment.alipay.notify-url:}")
    private String notifyUrl;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private OrderSkuInfoRepository orderSkuInfoRepository;
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    @Autowired
    private OrderPaymentInfoRepository orderPaymentInfoRepository;
    @Autowired
    private SubscriptionInfoService subscriptionInfoService;

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

        } catch (AlipayApiException e) {
            logger.error("调用支付宝预下单接口失败, orderId={}", orderId, e);
            throw new RuntimeException(e);
        }
        if (response.isSuccess()) {
            logger.info("调用成功");
        } else {
            logger.error("调用失败");
        }

        String qrCode = response.getQrCode();

        String imageData = translateQRCodeStringToBase64Image(qrCode);

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setOrderId(orderInfo.getOrderId());
        orderCreateDTO.setImageData(imageData);


        return orderCreateDTO;
    }


    private String buildNotifyUrl() {
        StringBuilder builder = new StringBuilder(notifyUrl);
        if (StringUtils.endsWith(notifyUrl, "/")) {
            builder.deleteCharAt(builder.length() - 1);
        }
        if (!StringUtils.startsWith(contextPath, "/")) {
            builder.append("/");
        }
        builder.append(contextPath);
        builder.append("/alipay/notify");
        return builder.toString();
//        return notifyUrl + "/" + contextPath + "/alipay/notify";
    }

    private String readPrivateKey() {
        return readFileContent(privateKeyFile);
    }

    public String readAlipayPublicKey() {
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

    public void handleNotify(Map<String, String> requestParamMap) {

        String method = MapUtils.getString(requestParamMap, "method");
        if (StringUtils.equals("alipay.trade.refund", method)) {
            // 退款通知
            refund(requestParamMap);
            return;
        }

        String notifyType = MapUtils.getString(requestParamMap, "notify_type");
        if (StringUtils.equals("trade_status_sync", notifyType)) {
            // 订单支付状态同步

            pay(requestParamMap);
            return;
        }

    }

    @Override
    public OrderRefundInfo createRefundOrder(OrderRefundInfo orderRefundInfo) {
        // 初始化SDK
        AlipayClient alipayClient = buildAlipayClient();

        // 构造请求参数以调用接口
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();

        // 设置商户订单号
        model.setOutTradeNo(orderRefundInfo.getOrderId());

        // 设置退款金额
        String refundAmount = BigDecimal.valueOf(orderRefundInfo.getOrderRefundAmount())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN)
                .toPlainString();
        model.setRefundAmount(refundAmount);

        // 设置退款原因说明
        model.setRefundReason("正常退款");

        // 设置退款请求号
        model.setOutRequestNo(orderRefundInfo.getRefundOrderId());

//        // 设置退款包含的商品列表信息
//        List<RefundGoodsDetail> refundGoodsDetail = new ArrayList<RefundGoodsDetail>();
//        RefundGoodsDetail refundGoodsDetail0 = new RefundGoodsDetail();
//        refundGoodsDetail0.setOutSkuId("outSku_01");
//        refundGoodsDetail0.setOutItemId("outItem_01");
//        refundGoodsDetail0.setGoodsId("apple-01");
//        refundGoodsDetail0.setRefundAmount("19.50");
//        List<String> outCertificateNoList = new ArrayList<String>();
//        outCertificateNoList.add("202407013232143241231243243423");
//        refundGoodsDetail0.setOutCertificateNoList(outCertificateNoList);
//        refundGoodsDetail.add(refundGoodsDetail0);
//        model.setRefundGoodsDetail(refundGoodsDetail);
//
//        // 设置退分账明细信息
//        List<OpenApiRoyaltyDetailInfoPojo> refundRoyaltyParameters = new ArrayList<OpenApiRoyaltyDetailInfoPojo>();
//        OpenApiRoyaltyDetailInfoPojo refundRoyaltyParameters0 = new OpenApiRoyaltyDetailInfoPojo();
//        refundRoyaltyParameters0.setAmount("0.1");
//        refundRoyaltyParameters0.setTransIn("2088101126708402");
//        refundRoyaltyParameters0.setRoyaltyType("transfer");
//        refundRoyaltyParameters0.setTransOut("2088101126765726");
//        refundRoyaltyParameters0.setTransOutType("userId");
//        refundRoyaltyParameters0.setRoyaltyScene("达人佣金");
//        refundRoyaltyParameters0.setTransInType("userId");
//        refundRoyaltyParameters0.setTransInName("张三");
//        refundRoyaltyParameters0.setDesc("分账给2088101126708402");
//        refundRoyaltyParameters.add(refundRoyaltyParameters0);
//        model.setRefundRoyaltyParameters(refundRoyaltyParameters);
//
//        // 设置查询选项
//        List<String> queryOptions = new ArrayList<String>();
//        queryOptions.add("refund_detail_item_list");
//        model.setQueryOptions(queryOptions);
//
//        // 设置针对账期交易
//        model.setRelatedSettleConfirmNo("2024041122001495000530302869");

        request.setBizModel(model);

        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            System.out.println("调用成功");
            orderRefundInfo.setTradeNo(response.getTradeNo());
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }

        return orderRefundInfo;
    }

    private AlipayClient buildAlipayClient() {
        String privateKey = readPrivateKey();
        String alipayPublicKey = readAlipayPublicKey();

        return new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "UTF-8", alipayPublicKey, "RSA2");
    }

    public Map<String, String> resolveRequestParam(PaymentNotifyHistory paymentNotifyHistory) {

        String requestParam = paymentNotifyHistory.getRequestBody();

        int l = 0;
        int r = 0;

        Map<String, String> paramMap = new HashMap<>();

        while (r < requestParam.length()) {
            char ch = requestParam.charAt(r);
            if (ch == '=') {
                String key = requestParam.substring(l, r);
                l = r + 1;
                r = l;
                while (r < requestParam.length() && requestParam.charAt(r) != '&') {
                    r++;
                }
                String value = requestParam.substring(l, r);
                key = URLDecoder.decode(key, StandardCharsets.UTF_8);
                value = URLDecoder.decode(value, StandardCharsets.UTF_8);
                paramMap.put(key, value);
                l = r + 1;
                r = l;
            } else {
                r++;
            }
        }

        return paramMap;
    }

    private void pay(Map<String, String> requestParamMap) {
        String orderId = requestParamMap.get("out_trade_no");

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderId, orderId);
        OrderInfo orderInfo = orderInfoRepository.selectOne(queryWrapper);

        String tradeStatus = MapUtils.getString(requestParamMap, "trade_status");

        switch (tradeStatus) {
            case "TRADE_SUCCESS":
            case "TRADE_FINISHED":
                // 支付成功
                String tradeNo = requestParamMap.get("trade_no");
                String totalAmountParam = requestParamMap.get("total_amount");
                BigDecimal totalAmount = new BigDecimal(totalAmountParam);
                Long orderPayAmount = totalAmount.multiply(BigDecimal.valueOf(100)).longValue();

                // gmt_payment
                String orderPayTimeText = requestParamMap.get("gmt_payment");
                Date orderPayTime = null;
                if (StringUtils.isNotBlank(orderPayTimeText)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        orderPayTime = sdf.parse(orderPayTimeText);
                    } catch (Exception e) {
                        logger.error("解析支付时间失败, orderId={}, gmt_payment={}", orderId, orderPayTimeText, e);
                        orderPayTime = new Date(); // 如果解析失败，将处理时候的时间作为支付时间
                    }
                }

                // 支付记录
                OrderPaymentInfo orderPaymentInfo = new OrderPaymentInfo();
                orderPaymentInfo.setOrderId(orderInfo.getOrderId());
                orderPaymentInfo.setOrderPayChannel("ALIPAY");
                orderPaymentInfo.setOrderPayAmount(orderPayAmount);
                orderPaymentInfo.setTradeNo(tradeNo);
                orderPaymentInfo.setOrderPayTime(orderPayTime);
                orderPaymentInfo.setStatus("TRADE_SUCCESS");
                orderPaymentInfoRepository.insert(orderPaymentInfo);


                // 修改订单状态为已支付
                LambdaUpdateWrapper<OrderInfo> orderInfoUpdate = new LambdaUpdateWrapper<>();
                orderInfoUpdate.eq(OrderInfo::getOrderId, orderId)
                        .set(OrderInfo::getOrderStatus, "TRADE_SUCCESS")
                        .set(OrderInfo::getOrderPaymentInfoId, orderPaymentInfo.getId());
                orderInfoRepository.update(orderInfoUpdate);


                // 延长订阅时间
                subscriptionInfoService.refreshExpireAt(orderPaymentInfo);

                break;
            case "TRADE_CLOSED":
                // 交易关闭
                throw new IllegalArgumentException("交易已关闭: " + orderId);
            default:
                throw new IllegalArgumentException("不支持的交易状态: " + tradeStatus);
        }


    }

    private void refund(Map<String, String> requestParamMap) {

    }

}
