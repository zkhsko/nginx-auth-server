package org.nginx.auth.service;

import org.nginx.auth.repository.PaymentHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author dongpo.li
 * @date 2024/12/23 15:27
 */
@Service
public class SubscriptionService {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    public PaymentHistoryRepository paymentHistoryRepository;

//    public Date calcExpireTime(Long accountId) {
//
//        List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.selectListByAccountId(accountId);
//
//        PriorityQueue<ComparablePaymentHistory> queue = new PriorityQueue<>();
//
//        for (PaymentHistory paymentHistory : paymentHistoryList) {
//
//            if (paymentHistory.getOrderPayTime() == null) {
//                continue;
//            }
//
//            ComparablePaymentHistory c = new ComparablePaymentHistory(paymentHistory);
//            queue.add(c);
//        }
//
//        Date expireAt = null;
//
//        while (!queue.isEmpty()) {
//
//            ComparablePaymentHistory comparable = queue.poll();
//
//            PaymentHistory paymentHistory = comparable.getPaymentHistory();
//
//            // TODO 改成Plan了
//            String planTimeUnit = paymentHistory.getProductTimeUnit();
//            Long planTimeValue = paymentHistory.getProductTimeValue();
//            Date orderPayTime = paymentHistory.getOrderPayTime();
//
//            LocalDateTime orderPayLocalDateTime = orderPayTime
//                    .toInstant()
//                    .atZone(ZoneId.systemDefault())
//                    .toLocalDateTime();
//
//            LocalDateTime orderExpireLocalDateTime = orderPayLocalDateTime;
//            if (StringUtils.equals("MONTH", planTimeUnit)) {
//                orderExpireLocalDateTime = orderPayLocalDateTime.plusMonths(planTimeValue);
//            } else if (StringUtils.equals("DAY", planTimeUnit)) {
//                orderExpireLocalDateTime = orderPayLocalDateTime.plusDays(planTimeValue);
//            }else if (StringUtils.equals("YEAR", planTimeUnit)) {
//                orderExpireLocalDateTime = orderPayLocalDateTime.plusYears(planTimeValue);
//            } else {
//                throw new IllegalArgumentException("unknown planTimeUnit " + planTimeUnit);
//            }
//
//            Duration duration = Duration.between(orderPayLocalDateTime, orderExpireLocalDateTime);
//            long millis = duration.toMillis();
//
//            Date subscribeExpireTime = new Date(orderPayTime.getTime() + millis);
//
////            boolean active = now.getTime() > orderPayTime.getTime()
////                    && now.getTime() < subscribeExpireTime.getTime();
////
////            if (active) {
////                return paymentHistory;
////            }
//
//
//        }
//
//        return expireAt;
//    }

}
