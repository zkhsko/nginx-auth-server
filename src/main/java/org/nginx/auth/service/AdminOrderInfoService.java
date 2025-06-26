package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.OrderDetailVO;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderSkuInfo;
import org.nginx.auth.repository.OrderInfoRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminOrderInfoService {

    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderInfoRepository orderInfoRepository;

    public BasicPaginationVO<OrderInfo> orderListPage(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        }

        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(OrderInfo::getId);
        PageHelper.startPage(page, size);
        List<OrderInfo> orderInfoList = orderInfoRepository.selectList(queryWrapper);

        PageInfo<OrderInfo> pageInfo = new PageInfo<>(orderInfoList);

        return BasicPaginationUtils.create(pageInfo);
    }

    public OrderDetailVO getOrderDetail(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            return null;
        }

        OrderInfo orderInfo = orderInfoService.selectByOrderId(orderId);
        if (orderInfo == null) {
            return null;
        }

        List<OrderSkuInfo> orderSkuInfoList = orderInfoService.selectOrderSkuInfoListByOrderId(orderId);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setOrderInfo(orderInfo);
        orderDetailVO.setOrderSkuInfoList(orderSkuInfoList);

        return orderDetailVO;
    }
}
