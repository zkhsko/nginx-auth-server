package org.nginx.auth.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.nginx.auth.enums.OrderRefundInfoStatusEnum;
import org.nginx.auth.model.OrderRefundInfo;
import org.nginx.auth.repository.OrderRefundInfoRepository;
import org.nginx.auth.service.OrderRefundInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 退款申请处理中定时任务
 * <p>
 *     定时获取退款处理中的退款信息,查询到退款成功之后更新到数据
 * </p>
 * <p>
 *     批量查询order_refund_info表中状态为TRADE_REFUND_PENDING的记录
 *     <br/>
 *     从支付宝接口中获取退款信息,退款成功之后更新到order_refund_info表中
 *     <br/>
 *     如果当前记录已退款成功,并且当前记录之前的所有记录都已处理完毕,记录当前记录的ID,下次从该ID开始查询
 * </p>
 */
@Component
public class OrderRefundPendingResolveSchedule {

    @Autowired
    private OrderRefundInfoRepository orderRefundInfoRepository;
    @Autowired
    private OrderRefundInfoService orderRefundInfoService;

    private volatile Long startId = 0L;

    private static final int BATCH_SIZE = 100;

    @Scheduled(cron = "0/1 * * * * ?")
    public void resolve() {

        // 从startId之后所有处理的退款记录都已退款成功,
        // 如果不是的话,之后的所有退款记录处理完之后都不再更新startId
        // 即下次任务还是从startId开始
        boolean resolvedFromStartId = true;

        while (true) {

            Page<OrderRefundInfo> page = new Page<>(1, BATCH_SIZE);
            LambdaQueryWrapper<OrderRefundInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.gt(OrderRefundInfo::getId, startId);
            queryWrapper.eq(OrderRefundInfo::getStatus, OrderRefundInfoStatusEnum.TRADE_REFUND_PENDING.name());
            queryWrapper.orderByAsc(OrderRefundInfo::getId);
            List<OrderRefundInfo> pendingList = orderRefundInfoRepository.selectPage(page, queryWrapper).getRecords();

            if (pendingList.isEmpty()) {
                break;
            }

            for (OrderRefundInfo pendingRefund : pendingList) {

                orderRefundInfoService.resolveOrderRefundInfoRefundPending(pendingRefund);

                // 如果退款成功了
                boolean isRefundSuccess = isRefundSuccess(pendingRefund);
                if (isRefundSuccess) {
                    if (resolvedFromStartId) {
                        this.startId = pendingRefund.getId();
                    }
                } else {
                    // 如果当前记录未退款成功,后续的记录都不再更新startId
                    resolvedFromStartId = false;
                }
            }

        }
    }

    private boolean isRefundSuccess(OrderRefundInfo refundInfo) {
        return OrderRefundInfoStatusEnum.TRADE_REFUND_SUCCESS.name().equals(refundInfo.getStatus());
    }

}
