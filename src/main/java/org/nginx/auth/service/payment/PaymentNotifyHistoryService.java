package org.nginx.auth.service.payment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.nginx.auth.enums.PaymentChannelEnum;
import org.nginx.auth.model.PaymentNotifyHistory;
import org.nginx.auth.repository.PaymentNotifyHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dongpo.li
 * @date 2023/12/20
 */
@Service
public class PaymentNotifyHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotifyHistoryService.class);

    @Autowired
    private PaymentNotifyHistoryRepository paymentNotifyHistoryRepository;

    public void insert(String notifyId, String orderPayChannel, String requestParam, String requestBody) {

        LambdaQueryWrapper<PaymentNotifyHistory> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(PaymentNotifyHistory::getNotifyId, notifyId);
        queryWrapper.eq(PaymentNotifyHistory::getOrderPayChannel, orderPayChannel);
        queryWrapper.last("FOR UPDATE");
        Long count = paymentNotifyHistoryRepository.selectCount(queryWrapper);
        if (count > 0) {
            // 同样的请求已经存在，说明是重复通知，直接返回
            return;
        }

        PaymentNotifyHistory paymentNotifyHistory = new PaymentNotifyHistory();
        paymentNotifyHistory.setNotifyId(notifyId);
        paymentNotifyHistory.setOrderPayChannel(orderPayChannel);
        paymentNotifyHistory.setRequestParam(requestParam);
        paymentNotifyHistory.setRequestBody(requestBody);
        paymentNotifyHistory.setResolved(false);

        paymentNotifyHistoryRepository.insert(paymentNotifyHistory);

    }

    /**
     * 每一条作为单独的事务处理,失败不影响之前已经处理成功的记录
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resolvePaymentNotify(long id) {
        logger.info("Resolving payment notify history id: {}", id);

//        LambdaQueryWrapper<PaymentNotifyHistory> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(PaymentNotifyHistory::getId, id);
//        queryWrapper.last("FOR UPDATE");
//        PaymentNotifyHistory paymentNotifyHistory = paymentNotifyHistoryRepository.selectOne(queryWrapper);

        resolve(id);
    }

    public void resolve(long id) {
        LambdaQueryWrapper<PaymentNotifyHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentNotifyHistory::getId, id);
        // 加锁,防止并发处理
        queryWrapper.last("FOR UPDATE");
        PaymentNotifyHistory paymentNotifyHistory = paymentNotifyHistoryRepository.selectOne(queryWrapper);

        if (paymentNotifyHistory.getResolved() != null && paymentNotifyHistory.getResolved()) {
            // 已经处理过了，直接返回
            logger.info("Payment notify history id: {} has already been resolved, skipping.", id);
            return;
        }

        String orderPayChannel = paymentNotifyHistory.getOrderPayChannel();
        PaymentChannelEnum paymentChannelEnum = PaymentChannelEnum.valueOf(orderPayChannel);
        PaymentService paymentService = PaymentServiceFactory.getPaymentService(paymentChannelEnum);

        // 解析请求参数
        Map<String, String> requestParamMap = paymentService.resolveRequestParam(paymentNotifyHistory);
        // 处理请求
        paymentService.handleNotify(requestParamMap);

        LambdaUpdateWrapper<PaymentNotifyHistory> paymentNotifyHistoryUpdate = new LambdaUpdateWrapper<>();
        paymentNotifyHistoryUpdate.set(PaymentNotifyHistory::getResolved, true);
        paymentNotifyHistoryUpdate.eq(PaymentNotifyHistory::getId, id);
        paymentNotifyHistoryRepository.update(paymentNotifyHistoryUpdate);

    }

}
