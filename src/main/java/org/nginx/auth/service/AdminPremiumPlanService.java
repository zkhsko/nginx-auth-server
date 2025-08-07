package org.nginx.auth.service;

import com.github.pagehelper.PageHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.dto.form.AdminPremiumPlanSkpCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanSkpUpdateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanSkuCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanSkuUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.AdminPremiumPlanSkpPageDataVO;
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
import java.util.ArrayList;
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

    public BasicPaginationVO<AdminPremiumPlanSkpPageDataVO> premiumPlanSkpListPage(int page, int size) {
        PageHelper.startPage(page, size, "id desc");
        List<PremiumPlanSkp> premiumPlanSkpList = premiumPlanSkpRepository.selectList(null);
        
        List<AdminPremiumPlanSkpPageDataVO> resultList = new ArrayList<>();
        for (PremiumPlanSkp skp : premiumPlanSkpList) {
            AdminPremiumPlanSkpPageDataVO vo = new AdminPremiumPlanSkpPageDataVO();
            vo.setPremiumPlanSkp(skp);
            resultList.add(vo);

            // 如果当前skp没有上架,始终不显示警告
            if (skp.getInUse() == null || !skp.getInUse()) {
                // 假装自己有合法的sku
                vo.setHasActiveSku(true);
                continue;
            }

            // 如果skp当前上架中,查询是否有上架中的sku,没有的话显示一个警告
            LambdaQueryWrapper<PremiumPlanSku> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PremiumPlanSku::getPremiumPlanSkpId, skp.getId());
            queryWrapper.eq(PremiumPlanSku::getInUse, true);
            PageHelper.startPage(1, 1); // 只需要查询一个结果
            PremiumPlanSku existsActiveSku = premiumPlanSkuRepository.selectOne(queryWrapper);
            vo.setHasActiveSku(existsActiveSku != null);

        }
        
        PageInfo<PremiumPlanSkp> pageInfo = new PageInfo<>(premiumPlanSkpList);

        BasicPaginationVO<AdminPremiumPlanSkpPageDataVO> voPageInfo = BasicPaginationUtils.copy(pageInfo);
        voPageInfo.setData(resultList);

        return voPageInfo;
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

    @Transactional
    public void createPremiumPlanSku(Long skpId, AdminPremiumPlanSkuCreateForm createForm) {
        PremiumPlanSku premiumPlanSku = new PremiumPlanSku();
        BeanCopyUtil.copy(createForm, premiumPlanSku);
        premiumPlanSku.setPremiumPlanSkpId(skpId);
        parsePremiumPlanPrice(premiumPlanSku, createForm.getPremiumPlanPrice());
        premiumPlanSkuRepository.insert(premiumPlanSku);
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

    @Transactional
    public void updatePremiumPlanSku(AdminPremiumPlanSkuUpdateForm updateForm) {
        PremiumPlanSku premiumPlanSku = new PremiumPlanSku();
        BeanCopyUtil.copy(updateForm, premiumPlanSku);
        parsePremiumPlanPrice(premiumPlanSku, updateForm.getPremiumPlanPrice());
        premiumPlanSkuRepository.updateById(premiumPlanSku);
    }

    @Transactional
    public void deletePremiumPlanSku(Long id) {
        premiumPlanSkuRepository.deleteById(id);
    }

    public PremiumPlanSkp selectPremiumPlanSkp(Long id) {
        return premiumPlanSkpRepository.selectById(id);
    }

    public PremiumPlanSku selectPremiumPlanSku(Long id) {
        return premiumPlanSkuRepository.selectById(id);
    }

    public List<PremiumPlanSku> selectPremiumPlanSkuListBySkpId(Long skpId) {
        LambdaQueryWrapper<PremiumPlanSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PremiumPlanSku::getPremiumPlanSkpId, skpId);
        return premiumPlanSkuRepository.selectList(queryWrapper);
    }

}
