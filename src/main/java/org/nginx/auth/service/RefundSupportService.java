package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.enums.OrderInfoStatusEnum;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.RefundSupport;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.RefundSupportRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.OrderInfoUtils;
import org.nginx.auth.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class RefundSupportService {

    @Autowired
    private RefundSupportRepository refundSupportRepository;
    @Autowired
    private OrderInfoService orderInfoService;

    public RefundSupport selectByRefundSupportId(String refundSupportId) {
        LambdaQueryWrapper<RefundSupport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundSupport::getRefundSupportId, refundSupportId);
        return refundSupportRepository.selectOne(queryWrapper);
    }

    public BasicPaginationVO<RefundSupport> refundSupportListPage(Integer page, Integer size) {
        PageHelper.startPage(page, size, "id desc");
        List<RefundSupport> refundSupportList = refundSupportRepository.selectList(null);

        PageInfo<RefundSupport> pageInfo = new PageInfo<>(refundSupportList);

        return BasicPaginationUtils.create(pageInfo);
    }

    public BasicPaginationVO<RefundSupport> refundSupportListPageByUserId(Integer page, Integer size) {
        User user = SessionUtil.getCurrentUser();
        if (user == null) {
            return BasicPaginationUtils.createEmpty();
        }

        LambdaQueryWrapper<RefundSupport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RefundSupport::getUserId, user.getId());
        PageHelper.startPage(page, size, "id desc");
        List<RefundSupport> refundSupportList = refundSupportRepository.selectList(queryWrapper);

        PageInfo<RefundSupport> pageInfo = new PageInfo<>(refundSupportList);

        return BasicPaginationUtils.create(pageInfo);
    }

    public void createRefundSupport(String orderId, Long refundAmount, Boolean refundPurchase, String refundReason) {

        User user = SessionUtil.getCurrentUser();
        if (user == null) {
            return;
        }

        Long userId = user.getId();
        String refundSupportId = OrderInfoUtils.generateOrderId(userId);

        // 检查订单是否存在
        OrderInfo orderInfo = orderInfoService.selectByOrderId(orderId);
        if (orderInfo == null) {
            throw new IllegalArgumentException("Order does not exist: " + orderId);
        }
        if (!Objects.equals(orderInfo.getUserId(), userId)) {
            throw new IllegalArgumentException("Order does not belong to the current user: " + orderId);
        }
        // 检查订单状态是否允许退款
        Set<String> allowedRefundStatusSet = Set.of(
                OrderInfoStatusEnum.PAYMENT_SUCCESS.name(),
                OrderInfoStatusEnum.TRADE_REFUND_SUCCESS.name()
        );
        if (!allowedRefundStatusSet.contains(orderInfo.getOrderStatus())) {
            throw new IllegalArgumentException("Order status does not allow refund: " + orderId);
        }


        RefundSupport refundSupport = new RefundSupport();
        refundSupport.setRefundSupportId(refundSupportId);
        refundSupport.setUserId(userId);
        refundSupport.setOrderId(orderId);
        refundSupport.setRefundAmount(refundAmount);
        refundSupport.setRefundPurchase(refundPurchase);
        refundSupport.setRefundReason(refundReason);
        refundSupport.setRemarkText("");

        // 保存退款申请
        refundSupportRepository.insert(refundSupport);
    }

}
