package org.nginx.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.PremiumPlanSkpVO;
import org.nginx.auth.dto.vo.PremiumPlanSkuVO;
import org.nginx.auth.model.PremiumPlanSkp;
import org.nginx.auth.model.PremiumPlanSku;
import org.nginx.auth.repository.PremiumPlanSkpRepository;
import org.nginx.auth.repository.PremiumPlanSkuRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PremiumPlanService {

    @Autowired
    private PremiumPlanSkpRepository premiumPlanSkpRepository;
    @Autowired
    private PremiumPlanSkuRepository premiumPlanSkuRepository;

    public BasicPaginationVO<PremiumPlanSkpVO> premiumPlanListPage(Integer page, Integer size) {

        PageHelper.startPage(page, size);
        LambdaQueryWrapper<PremiumPlanSkp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(PremiumPlanSkp::getId);
        queryWrapper.eq(PremiumPlanSkp::getInUse, true);
        List<PremiumPlanSkp> premiumPlanSkpList = premiumPlanSkpRepository.selectList(queryWrapper);

        PageInfo<PremiumPlanSkp> pageInfo = new PageInfo<>(premiumPlanSkpList);

        List<Long> skpIds = premiumPlanSkpList
                .stream()
                .map(PremiumPlanSkp::getId)
                .collect(Collectors.toList());

        Map<Long, List<PremiumPlanSku>> premiumPlanSkuMap = selectSkuListGroupBySkpId(skpIds);

        PageInfo<PremiumPlanSkpVO> voPageInfo = new PageInfo<>();
        List<PremiumPlanSkp> data = pageInfo.getList();
        List<PremiumPlanSkpVO> skpVoList = new ArrayList<>(data.size());
        for (PremiumPlanSkp premiumPlan : data) {
            PremiumPlanSkpVO skpVo = new PremiumPlanSkpVO();
            skpVoList.add(skpVo);

            skpVo.setPremiumPlanSkp(premiumPlan);
            List<PremiumPlanSku> skuList = premiumPlanSkuMap.get(premiumPlan.getId());
            if (CollectionUtils.isEmpty(skuList)) {
                skpVo.setPremiumPlanSkuList(new ArrayList<>());
                continue;
            }

            List<PremiumPlanSkuVO> skuVoList = new ArrayList<>(skuList.size());
            for (PremiumPlanSku premiumPlanSku : skuList) {
                PremiumPlanSkuVO skuVo = new PremiumPlanSkuVO();
                skuVo.setPremiumPlanSku(premiumPlanSku);
                BigDecimal priceYuan = BigDecimal.valueOf(premiumPlanSku.getPremiumPlanPrice())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
                skuVo.setPremiumPlanPriceYuan(priceYuan.toString());
                skuVoList.add(skuVo);
            }
            skpVo.setPremiumPlanSkuList(skuVoList);
        }
        BeanCopyUtil.copy(pageInfo, voPageInfo);
        voPageInfo.setList(skpVoList);

        return BasicPaginationUtils.create(voPageInfo);
    }

    private Map<Long, List<PremiumPlanSku>> selectSkuListGroupBySkpId(Collection<Long> skpIdList) {

        if (skpIdList == null || skpIdList.isEmpty()) {
            return Map.of();
        }

        LambdaQueryWrapper<PremiumPlanSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PremiumPlanSku::getPremiumPlanSkpId, skpIdList);
        queryWrapper.eq(PremiumPlanSku::getInUse, true);
        List<PremiumPlanSku> premiumPlanSkpList = premiumPlanSkuRepository.selectList(queryWrapper);

        return premiumPlanSkpList
                .stream()
                .collect(Collectors.groupingBy(PremiumPlanSku::getPremiumPlanSkpId));
    }

}
