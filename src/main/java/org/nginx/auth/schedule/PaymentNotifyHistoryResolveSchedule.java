package org.nginx.auth.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import org.nginx.auth.model.PaymentNotifyHistory;
import org.nginx.auth.repository.PaymentNotifyHistoryRepository;
import org.nginx.auth.service.payment.PaymentNotifyHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2023/12/19
 */
@Component
public class PaymentNotifyHistoryResolveSchedule {
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotifyHistoryResolveSchedule.class);

    private long startId = 0L;

    @Autowired
    private PaymentNotifyHistoryRepository paymentNotifyHistoryRepository;
    @Autowired
    private PaymentNotifyHistoryService paymentNotifyHistoryService;

    /**
     * 每秒执行一次
     */
//    @Scheduled(cron = "0/1 * * * * ?")
    public void resolve() {
        logger.info("PaymentNotifyHistoryResolveSchedule.resolve start");

        int batchSize = 100;

        while (true) {
            LambdaQueryWrapper<PaymentNotifyHistory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(PaymentNotifyHistory::getId, PaymentNotifyHistory::getResolved);
            queryWrapper.gt(PaymentNotifyHistory::getId, startId);
            queryWrapper.orderByAsc(PaymentNotifyHistory::getId);
            queryWrapper.last("LIMIT " + batchSize);
            List<PaymentNotifyHistory> batchList =
                    paymentNotifyHistoryRepository.selectList(queryWrapper);
            if (batchList.isEmpty()) {
                // 没有更多记录，结束
                break;
            }


            for (PaymentNotifyHistory history : batchList) {
                if (history.getResolved() != null && history.getResolved()) {
                    startId = history.getId();
                    continue;
                }

                // 开始处理未处理记录
                logger.info("Processing PaymentNotifyHistory id: {}", history.getId());
                paymentNotifyHistoryService.resolvePaymentNotify(history.getId());


            }


        }

        logger.info("PaymentNotifyHistoryResolveSchedule.resolve end, updated startId: {}", startId);
    }

}
