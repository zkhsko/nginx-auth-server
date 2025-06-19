package org.nginx.auth.service;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.model.PaymentHistory;
import org.nginx.auth.model.PlanInfo;
import org.nginx.auth.repository.PaymentHistoryRepository;
import org.nginx.auth.repository.ProductInfoRepository;
import org.nginx.auth.request.OrderCreateParam;
import org.nginx.auth.response.OrderCreateDTO;
import org.nginx.auth.service.payment.PaymentService;
import org.nginx.auth.service.payment.PaymentServiceFactory;
import org.nginx.auth.util.OrderInfoUtils;

/**
 * @author dongpo.li
 * @date 2023/12/20
 */
@Service
public class OrderInfoService {
    private static final Logger logger = LoggerFactory.getLogger(OrderInfoService.class);

    @Resource
    private ProductInfoRepository productInfoRepository;
    @Resource
    private PaymentHistoryRepository paymentHistoryRepository;

    public OrderCreateDTO createOrder(PaymentChannelEnum paymentChannelEnum, OrderCreateParam param) {

        long productId = NumberUtils.toLong(param.getProductId(), 0);

        PlanInfo planInfo = productInfoRepository.selectById(productId);
        if (planInfo == null || planInfo.getInUse() == null || !planInfo.getInUse()) {
            logger.info("商品不存在,下单失败, productId={}", productId);
            return null;
        }

        int reduceStock = productInfoRepository.reduceStock(planInfo.getId());
        if (reduceStock <= 0) {
            logger.info("下单扣减库存失败,应该是没有库存了, productId={}", productId);
            return null;
        }

        Long userId = 3L;

        PaymentHistory paymentHistoryInsert = new PaymentHistory();
        OrderInfoUtils.PRODUCT_INFO_2_PAYMENT_INFO
                .copy(planInfo, paymentHistoryInsert, null);

        String orderId = OrderInfoUtils.generateOrderId(userId);
        paymentHistoryInsert.setId(null);
        paymentHistoryInsert.setAccountId(userId);
        paymentHistoryInsert.setOrderId(orderId);
        paymentHistoryInsert.setProductId(planInfo.getId());
        paymentHistoryInsert.setOrderPayChannel(paymentChannelEnum.name());
        paymentHistoryRepository.insert(paymentHistoryInsert);

        // 创建支付平台订单,获取支付二维码
        OrderCreateDTO rsp = new OrderCreateDTO();
        rsp.setOrderId(orderId);

        switch (paymentChannelEnum) {
            case ALIPAY: {
                PaymentService paymentService = PaymentServiceFactory.getPaymentService(paymentChannelEnum);
                OrderCreateDTO orderCreateDTO = paymentService.createOrder(paymentHistoryInsert);
                rsp.setImageData(orderCreateDTO.getImageData());
                break;
            }
            case WECHAT_PAY: {

                break;
            }
        }

        return rsp;

    }

}
