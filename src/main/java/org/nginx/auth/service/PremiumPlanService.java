package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PremiumPlan;
import org.nginx.auth.repository.PremiumPlanRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PremiumPlanService {

    @Autowired
    private PremiumPlanRepository premiumPlanRepository;

    public BasicPaginationVO<PremiumPlan> premiumPlanListPage(Integer page, Integer size) {

        PageHelper.startPage(page, size);
        LambdaQueryWrapper<PremiumPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(PremiumPlan::getId);
        queryWrapper.eq(PremiumPlan::getInUse, true);
        List<PremiumPlan> planInfoList = premiumPlanRepository.selectList(queryWrapper);

        PageInfo<PremiumPlan> pageInfo = new PageInfo<>(planInfoList);

        return BasicPaginationUtils.create(pageInfo);
    }

}
