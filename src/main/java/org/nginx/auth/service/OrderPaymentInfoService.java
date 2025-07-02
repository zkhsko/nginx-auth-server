package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.repository.OrderPaymentInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderPaymentInfoService {

    @Autowired
    private OrderPaymentInfoRepository orderPaymentInfoRepository;

    public List<OrderPaymentInfo> selectListByOrderId(String orderId) {
        LambdaQueryWrapper<OrderPaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderPaymentInfo::getOrderId, orderId);
        return orderPaymentInfoRepository.selectList(queryWrapper);
    }

}
