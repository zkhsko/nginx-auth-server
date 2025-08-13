package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.OrderDetailVO;
import org.nginx.auth.enums.OrderInfoStatusEnum;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.model.*;
import org.nginx.auth.repository.*;
import org.nginx.auth.request.OrderCreateParam;
import org.nginx.auth.response.OrderCreateDTO;
import org.nginx.auth.service.payment.PaymentService;
import org.nginx.auth.service.payment.PaymentServiceFactory;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.OrderInfoUtils;
import org.nginx.auth.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dongpo.li
 * @date 2023/12/20
 */
@Service
public class OrderInfoService {
    private static final Logger logger = LoggerFactory.getLogger(OrderInfoService.class);

    @Autowired
    private PremiumPlanSkpRepository premiumPlanSkpRepository;
    @Autowired
    private PremiumPlanSkuRepository premiumPlanSkuRepository;
    @Autowired
    private OrderPaymentInfoService orderPaymentInfoService;
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    @Autowired
    private OrderSkuInfoRepository orderSkuInfoRepository;
    @Autowired
    private OrderPaymentInfoRepository orderPaymentInfoRepository;
    @Autowired
    private OrderRefundInfoRepository orderRefundInfoRepository;
    @Autowired
    private RefundSupportRepository refundSupportRepository;

    public boolean orderExists(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return false;
        }

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderId, orderId);

        return orderInfoRepository.selectCount(queryWrapper) > 0;
    }

    public String createOrder(List<OrderCreateParam> paramList) {

        if (CollectionUtils.isEmpty(paramList)) {
            logger.info("下单参数错误, paramList is empty");
            return "";
        }


        // TODO 校验是否合法商品 存在 上架 库存 等信息

        User user = SessionUtil.getCurrentUser();
        if (user == null) {
            // 其实也没啥必要,之前有拦截器判断过了,这里加上是因为不加的话Idea会报警告
            throw new IllegalArgumentException("用户未登录");
        }

        Long userId = user.getId();

        String orderId = OrderInfoUtils.generateOrderId(userId);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(null);
        orderInfo.setOrderId(orderId);
        orderInfo.setUserId(userId);
        orderInfo.setOrderAmount(0L);
        orderInfo.setOrderStatus(OrderInfoStatusEnum.PENDING_PAYMENT.name());
        orderInfo.setOrderCreateTime(new Date());

        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        List<OrderSkuInfo> orderSkuInfoList = new ArrayList<>();

        for (OrderCreateParam param : paramList) {

            long skuId = Long.parseLong(param.getSkuId());
            PremiumPlanSku premiumPlanSku = premiumPlanSkuRepository.selectById(skuId);
            PremiumPlanSkp premiumPlanSkp = premiumPlanSkpRepository.selectById(premiumPlanSku.getPremiumPlanSkpId());

            int reduceStock = premiumPlanSkuRepository.reduceStock(premiumPlanSku.getId());
            if (reduceStock <= 0) {
                logger.info("下单扣减库存失败,应该是没有库存了, skuId={}", param.getSkuId());
                // 手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return "";
            }

            OrderSkuInfo orderSkuInfo = new OrderSkuInfo();
            orderSkuInfo.setOrderId(orderId);
            orderSkuInfo.setPremiumPlanSkpId(premiumPlanSkp.getId());
            orderSkuInfo.setPremiumPlanSkuId(premiumPlanSku.getId());
            orderSkuInfo.setPremiumPlanName(premiumPlanSkp.getPremiumPlanName());
            orderSkuInfo.setPremiumPlanDesc(premiumPlanSkp.getPremiumPlanDesc());
            orderSkuInfo.setPremiumPlanSkuName(premiumPlanSku.getPremiumPlanSkuName());
            orderSkuInfo.setPremiumPlanPrice(premiumPlanSku.getPremiumPlanPrice());
            orderSkuInfo.setPremiumPlanTimeUnit(premiumPlanSku.getPremiumPlanTimeUnit());
            orderSkuInfo.setPremiumPlanTimeValue(premiumPlanSku.getPremiumPlanTimeValue());
            orderSkuInfo.setCnt(param.getCnt());
            orderSkuInfoList.add(orderSkuInfo);


            totalOrderAmount = totalOrderAmount.add(BigDecimal.valueOf(premiumPlanSku.getPremiumPlanPrice() * param.getCnt()));

        }

        orderInfo.setOrderAmount(totalOrderAmount.longValue());
        orderInfoRepository.insert(orderInfo);

        orderSkuInfoRepository.insert(orderSkuInfoList);

        return orderId;


//        orderPaymentInfoInsert.setId(null);
//        orderPaymentInfoInsert.setUserId(userId);
//        orderPaymentInfoInsert.setOrderId(orderId);
//        orderPaymentInfoInsert.setPremiumPlanId(premiumPlan.getId());
//        orderPaymentInfoInsert.setOrderPayChannel(paymentChannelEnum.name());
//        paymentHistoryRepository.insert(orderPaymentInfoInsert);
//
//        // 创建支付平台订单,获取支付二维码
//        OrderCreateDTO rsp = new OrderCreateDTO();
//        rsp.setOrderId(orderId);
//
//        switch (paymentChannelEnum) {
//            case ALIPAY: {
//                PaymentService paymentService = PaymentServiceFactory.getPaymentService(paymentChannelEnum);
//                OrderCreateDTO orderCreateDTO = paymentService.createOrder(orderPaymentInfoInsert);
//                rsp.setImageData(orderCreateDTO.getImageData());
//                break;
//            }
//            case WECHAT_PAY: {
//
//                break;
//            }
//        }
//
//        return rsp;

    }

    public OrderDetailVO getOrderDetail(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return null;
        }

        OrderInfo orderInfo = this.selectByOrderId(orderId);
        if (orderInfo == null) {
            return null;
        }

        List<OrderSkuInfo> orderSkuInfoList = this.selectOrderSkuInfoListByOrderId(orderId);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderInfo(orderInfo);
        orderDetailVO.setOrderSkuInfoList(orderSkuInfoList);

        // payment list
        LambdaQueryWrapper<OrderPaymentInfo> paymentQuery = new LambdaQueryWrapper<>();
        paymentQuery.eq(OrderPaymentInfo::getOrderId, orderId);
        List<OrderPaymentInfo> paymentList = orderPaymentInfoRepository.selectList(paymentQuery);
        orderDetailVO.setPaymentList(paymentList);

        // refund history
        LambdaQueryWrapper<OrderRefundInfo> refundQuery = new LambdaQueryWrapper<>();
        refundQuery.eq(OrderRefundInfo::getOrderId, orderId);
        List<OrderRefundInfo> refundHistory = orderRefundInfoRepository.selectList(refundQuery);
        orderDetailVO.setRefundHistory(refundHistory);

        // refund support list
        LambdaQueryWrapper<RefundSupport> refundSupportQuery = new LambdaQueryWrapper<>();
        refundSupportQuery.eq(RefundSupport::getOrderId, orderId);
        List<RefundSupport> refundSupportList = refundSupportRepository.selectList(refundSupportQuery);
        orderDetailVO.setRefundSupportList(refundSupportList);

        return orderDetailVO;
    }

    public OrderInfo selectByOrderId(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return null;
        }

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderId, orderId);

        return orderInfoRepository.selectOne(queryWrapper);
    }

    public List<OrderSkuInfo> selectOrderSkuInfoListByOrderId(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return null;
        }

        LambdaQueryWrapper<OrderSkuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderSkuInfo::getOrderId, orderId);
        queryWrapper.orderBy(true, true, OrderSkuInfo::getId);

        return orderSkuInfoRepository.selectList(queryWrapper);
    }

    public String pay(String orderId, String paymentChannel) {


        boolean validEnum = EnumUtils.isValidEnum(PaymentChannelEnum.class, paymentChannel);
        if (!validEnum) {
            throw new IllegalArgumentException("支付渠道不合法");
        }
        PaymentChannelEnum paymentChannelEnum = EnumUtils.getEnum(PaymentChannelEnum.class, paymentChannel);

        OrderInfo orderInfo = this.selectByOrderId(orderId);
        if (orderInfo == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        boolean createNewPayment = true;
        // 查看该订单其他支付记录
        List<OrderPaymentInfo> orderPaymentInfoList = orderPaymentInfoService.selectListByOrderId(orderId);
        for (OrderPaymentInfo orderPaymentInfo : orderPaymentInfoList) {
            // 如果有订单已经支付过了,不允许修改了
            if (orderPaymentInfo.getOrderPayAmount() > 0) {
                throw new IllegalArgumentException("订单已经支付成功,不允许重复支付");
            }
        }

        // 创建支付平台订单,获取支付二维码
        OrderCreateDTO rsp = new OrderCreateDTO();
        rsp.setOrderId(orderId);

        switch (paymentChannelEnum) {
            case ALIPAY: {
                PaymentService paymentService = PaymentServiceFactory.getPaymentService(paymentChannelEnum);
                OrderCreateDTO orderCreateDTO = paymentService.createOrder(orderInfo);
                rsp.setImageData(orderCreateDTO.getImageData());
                break;
            }
            case WECHAT_PAY: {

                break;
            }
        }

        return rsp.getImageData();


    }

    public BasicPaginationVO<OrderInfo> orderListPage(User user, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getUserId, user.getId());
        queryWrapper.orderByDesc(OrderInfo::getId);
        List<OrderInfo> orderInfoList = orderInfoRepository.selectList(queryWrapper);
        PageInfo<OrderInfo> pageInfo = new PageInfo<>(orderInfoList);
        return BasicPaginationUtils.create(pageInfo);
    }
}
