package org.nginx.auth.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.nginx.auth.dto.form.AdminPremiumPlanPredicateCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanPredicateUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PremiumPlanPredicate;
import org.nginx.auth.repository.PremiumPlanPredicateRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminPremiumPlanPredicateService {

    @Autowired
    private PremiumPlanPredicateRepository premiumPlanPredicateRepository;

    public BasicPaginationVO<PremiumPlanPredicate> predicateListPage(int page, int size) {
        PageHelper.startPage(page, size, "id desc");
        List<PremiumPlanPredicate> predicateList = premiumPlanPredicateRepository.selectList(null);
        PageInfo<PremiumPlanPredicate> pageInfo = new PageInfo<>(predicateList);
        return BasicPaginationUtils.create(pageInfo);
    }

    public void createPredicate(AdminPremiumPlanPredicateCreateForm createForm) {
        PremiumPlanPredicate predicate = new PremiumPlanPredicate();
        BeanCopyUtil.copy(createForm, predicate);
        premiumPlanPredicateRepository.insert(predicate);
    }

    public void updatePredicate(AdminPremiumPlanPredicateUpdateForm updateForm) {
        PremiumPlanPredicate predicate = new PremiumPlanPredicate();
        BeanCopyUtil.copy(updateForm, predicate);
        premiumPlanPredicateRepository.updateById(predicate);
    }

    public void deletePredicate(Long id) {
        premiumPlanPredicateRepository.deleteById(id);
    }

    public PremiumPlanPredicate getPredicate(Long id) {
        return premiumPlanPredicateRepository.selectById(id);
    }
}