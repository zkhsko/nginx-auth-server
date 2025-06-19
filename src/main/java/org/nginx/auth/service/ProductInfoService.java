package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PlanInfo;
import org.nginx.auth.repository.ProductInfoRepository;
import org.nginx.auth.util.BasicPaginationUtils;

import java.util.List;

@Service
public class ProductInfoService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    public BasicPaginationVO<PlanInfo> productListPage(Integer page, Integer size) {

        PageHelper.startPage(page, size);
        LambdaQueryWrapper<PlanInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(PlanInfo::getId);
        queryWrapper.eq(PlanInfo::getInUse, true);
        List<PlanInfo> planInfoList = productInfoRepository.selectList(queryWrapper);

        PageInfo<PlanInfo> pageInfo = new PageInfo<>(planInfoList);

        return BasicPaginationUtils.create(pageInfo);
    }

}
