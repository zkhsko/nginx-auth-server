package org.nginx.auth.service;

import com.github.pagehelper.PageHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.dto.form.AdminPremiumPlanSkpCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanSkpUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PremiumPlanSkp;
import org.nginx.auth.model.PremiumPlanSku;
import org.nginx.auth.repository.PremiumPlanSkpRepository;
import org.nginx.auth.repository.PremiumPlanSkuRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author dongpo.li
 * @date 2024/12/28 19:39
 */
@Service
public class AdminPremiumPlanService {

    @Autowired
    private PremiumPlanSkpRepository premiumPlanSkpRepository;
    @Autowired
    private PremiumPlanSkuRepository premiumPlanSkuRepository;

    public BasicPaginationVO<PremiumPlanSkp> premiumPlanSkpListPage(int page, int size) {
        PageHelper.startPage(page, size, "id desc");
        List<PremiumPlanSkp> premiumPlanSkpList = premiumPlanSkpRepository.selectList(null);
        PageInfo<PremiumPlanSkp> pageInfo = new PageInfo<>(premiumPlanSkpList);
        return BasicPaginationUtils.create(pageInfo);
    }

    @Transactional
    public void createPremiumPlanSkp(AdminPremiumPlanSkpCreateForm premiumPlanCreateForm) {
        PremiumPlanSkp premiumPlanSkp = new PremiumPlanSkp();
        BeanCopyUtil.copy(premiumPlanCreateForm, premiumPlanSkp);
        buildPredicateListText(premiumPlanSkp, premiumPlanCreateForm.getPredicateList());
        premiumPlanSkpRepository.insert(premiumPlanSkp);

//        PremiumPlanSku premiumPlanSku = new PremiumPlanSku();
//        BeanCopyUtil.copy(premiumPlanCreateForm, premiumPlanSku);
//        BigDecimal premiumPlanPrice = premiumPlanCreateForm.getPremiumPlanPrice();
//        parsePremiumPlanPrice(premiumPlanSku, premiumPlanPrice);
//        premiumPlanSku.setPremiumPlanSkpId(premiumPlanSkp.getId());
//        premiumPlanSkuRepository.insert(premiumPlanSku);
    }

    private void parsePremiumPlanPrice(PremiumPlanSku premiumPlan, BigDecimal price) {
        long priceLong = price
                .multiply(new BigDecimal(100))
                .longValue();
        premiumPlan.setPremiumPlanPrice(priceLong);
    }

    private void buildPredicateListText(PremiumPlanSkp premiumPlan, LinkedHashSet<Long> predicateList) {
        String predicateListText = StringUtils.join(predicateList, ",");
        premiumPlan.setPredicateListText(predicateListText);
    }

    @Transactional
    public void updatePremiumPlanSkp(AdminPremiumPlanSkpUpdateForm premiumPlanUpdateForm) {
        PremiumPlanSkp premiumPlanSkp = new PremiumPlanSkp();
        BeanCopyUtil.copy(premiumPlanUpdateForm, premiumPlanSkp);
        buildPredicateListText(premiumPlanSkp, premiumPlanUpdateForm.getPredicateList());
        premiumPlanSkpRepository.updateById(premiumPlanSkp);

//        // TODO: 目前只支持一个skp对应一个sku
//        PremiumPlanSku premiumPlanSku = premiumPlanSkuRepository.selectOne(new LambdaQueryWrapper<PremiumPlanSku>()
//                .eq(PremiumPlanSku::getPremiumPlanSkpId, premiumPlanUpdateForm.getId()));
//        if (premiumPlanSku != null) {
//            BeanCopyUtil.copy(premiumPlanUpdateForm, premiumPlanSku);
//            BigDecimal premiumPlanPrice = premiumPlanUpdateForm.getPremiumPlanPrice();
//            parsePremiumPlanPrice(premiumPlanSku, premiumPlanPrice);
//            premiumPlanSkuRepository.updateById(premiumPlanSku);
//        }
    }

    @Transactional
    public void deletePremiumPlanSkp(Long id) {
        // TODO 要假删,数据还有用
        premiumPlanSkpRepository.deleteById(id);

        // 一样要假删
        LambdaQueryWrapper<PremiumPlanSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PremiumPlanSku::getPremiumPlanSkpId, id);
        premiumPlanSkuRepository.delete(queryWrapper);
    }

    public PremiumPlanSkp selectPremiumPlanSkp(Long id) {
        return premiumPlanSkpRepository.selectById(id);
    }

    public List<PremiumPlanSku> selectPremiumPlanSkuListBySkpId(Long skpId) {
        LambdaQueryWrapper<PremiumPlanSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PremiumPlanSku::getPremiumPlanSkpId, skpId);
        return premiumPlanSkuRepository.selectList(queryWrapper);
    }

}
