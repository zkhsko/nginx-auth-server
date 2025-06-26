package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.repository.OrderInfoRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminOrderInfoService {

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

}
