package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.PremiumPlanVO;
import org.nginx.auth.model.PremiumPlan;
import org.nginx.auth.repository.PremiumPlanRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PremiumPlanService {

    @Autowired
    private PremiumPlanRepository premiumPlanRepository;

    public BasicPaginationVO<PremiumPlanVO> premiumPlanListPage(Integer page, Integer size) {

        PageHelper.startPage(page, size);
        LambdaQueryWrapper<PremiumPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(PremiumPlan::getId);
        queryWrapper.eq(PremiumPlan::getInUse, true);
        List<PremiumPlan> premiumPlanList = premiumPlanRepository.selectList(queryWrapper);

        PageInfo<PremiumPlan> pageInfo = new PageInfo<>(premiumPlanList);

        PageInfo<PremiumPlanVO> voPageInfo = new PageInfo<>();
        List<PremiumPlan> data = pageInfo.getList();
        List<PremiumPlanVO> voList = new ArrayList<>(data.size());
        for (PremiumPlan premiumPlan : data) {
            PremiumPlanVO vo = new PremiumPlanVO();
            vo.setPremiumPlan(premiumPlan);
            BigDecimal priceYuan = BigDecimal.valueOf(premiumPlan.getPremiumPlanPrice())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
            vo.setPremiumPlanPriceYuan(priceYuan.toString());
            voList.add(vo);
        }
        BeanCopyUtil.copy(pageInfo, voPageInfo);
        voPageInfo.setList(voList);

        return BasicPaginationUtils.create(voPageInfo);
    }

}
