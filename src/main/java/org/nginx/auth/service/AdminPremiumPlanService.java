package org.nginx.auth.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.dto.form.AdminPremiumPlanCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PremiumPlan;
import org.nginx.auth.repository.PremiumPlanRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private PremiumPlanRepository premiumPlanRepository;

    public BasicPaginationVO<PremiumPlan> premiumPlanListPage(int page, int size) {
        PageHelper.startPage(page, size, "id desc");
        List<PremiumPlan> premiumPlanList = premiumPlanRepository.selectList(null);
        PageInfo<PremiumPlan> pageInfo = new PageInfo<>(premiumPlanList);
        return BasicPaginationUtils.create(pageInfo);
    }

    public void createPremiumPlan(AdminPremiumPlanCreateForm premiumPlanCreateForm) {

        PremiumPlan premiumPlan = new PremiumPlan();
        BeanCopyUtil.copy(premiumPlanCreateForm, premiumPlan);
        BigDecimal premiumPlanPrice = premiumPlanCreateForm.getPremiumPlanPrice();
        parsePremiumPlanPrice(premiumPlan, premiumPlanPrice);
        parsePredicateListText(premiumPlan, premiumPlanCreateForm.getPredicateList());
        premiumPlan.setPremiumPlanStock(premiumPlanCreateForm.getPremiumPlanStock());
        premiumPlan.setPremiumPlanTimeValue(premiumPlanCreateForm.getPremiumPlanTimeValue());

        premiumPlanRepository.insert(premiumPlan);
    }

    private void parsePremiumPlanPrice(PremiumPlan premiumPlan, BigDecimal price) {
        long priceLong = price
                .multiply(new BigDecimal(100))
                .longValue();
        premiumPlan.setPremiumPlanPrice(priceLong);
    }

    // TODO 方法名
    private void parsePredicateListText(PremiumPlan premiumPlan, LinkedHashSet<Long> predicateList) {
        String predicateListText = StringUtils.join(predicateList, ",");
        premiumPlan.setPredicateListText(predicateListText);
    }

    public void updatePremiumPlan(AdminPremiumPlanUpdateForm premiumPlanUpdateForm) {

        PremiumPlan premiumPlan = new PremiumPlan();
        BeanCopyUtil.copy(premiumPlanUpdateForm, premiumPlan);
        BigDecimal premiumPlanPrice = premiumPlanUpdateForm.getPremiumPlanPrice();
        parsePremiumPlanPrice(premiumPlan, premiumPlanPrice);
        parsePredicateListText(premiumPlan, premiumPlanUpdateForm.getPredicateList());
        premiumPlan.setPremiumPlanStock(premiumPlanUpdateForm.getPremiumPlanStock());
        premiumPlan.setPremiumPlanTimeValue(premiumPlanUpdateForm.getPremiumPlanTimeValue());

        premiumPlanRepository.updateById(premiumPlan);
    }

    public void deletePremiumPlan(Long id) {
        premiumPlanRepository.deleteById(id);
    }

    public PremiumPlan getPremiumPlan(Long id) {
        return premiumPlanRepository.selectById(id);
    }

}
