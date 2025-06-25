package org.nginx.auth.service.payment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.nginx.auth.model.PaymentNotifyHistory;
import org.nginx.auth.repository.PaymentNotifyHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
