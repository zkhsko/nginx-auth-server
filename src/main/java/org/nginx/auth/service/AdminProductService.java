package org.nginx.auth.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.nginx.auth.dto.form.AdminProductInfoCreateForm;
import org.nginx.auth.dto.form.AdminProductInfoUpdateForm;
import org.nginx.auth.model.PlanInfo;
import org.nginx.auth.repository.ProductInfoRepository;
import org.nginx.auth.util.BasicPaginationUtils;
import org.nginx.auth.util.BeanCopyUtil;
import org.nginx.auth.dto.vo.BasicPaginationVO;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author dongpo.li
 * @date 2024/12/28 19:39
 */
@Service
public class AdminProductService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    public BasicPaginationVO<PlanInfo> productListPage(int page, int size) {
        PageHelper.startPage(page, size, "id desc");
        List<PlanInfo> planInfoList = productInfoRepository.selectList(null);
        PageInfo<PlanInfo> pageInfo = new PageInfo<>(planInfoList);
        return BasicPaginationUtils.create(pageInfo);
    }

    public void createProduct(AdminProductInfoCreateForm productInfoCreateForm) {

        PlanInfo planInfo = new PlanInfo();
        BeanCopyUtil.copy(productInfoCreateForm, planInfo);
        BigDecimal productPrice = new BigDecimal(productInfoCreateForm.getProductPrice());
        parseProductPrice(planInfo, productPrice);
        parseRouteListText(planInfo, productInfoCreateForm.getRouteList());
        planInfo.setStock(NumberUtils.toInt(productInfoCreateForm.getProductStock()));
        planInfo.setPlanTimeValue(NumberUtils.toLong(productInfoCreateForm.getProductTimeValue()));

        productInfoRepository.insert(planInfo);
    }

    private void parseProductPrice(PlanInfo planInfo, BigDecimal price) {
        long priceLong = price
                .multiply(new BigDecimal(100))
                .longValue();
        planInfo.setPrice(priceLong);
    }

    private void parseRouteListText(PlanInfo planInfo, LinkedHashSet<Long> routeList) {
        String routeListText = StringUtils.join(routeList, ",");
        planInfo.setRouteListText(routeListText);
    }

    public void updateProduct(AdminProductInfoUpdateForm productInfoUpdateForm) {

        PlanInfo planInfo = new PlanInfo();
        BeanCopyUtil.copy(productInfoUpdateForm, planInfo);
        BigDecimal productPrice = new BigDecimal(productInfoUpdateForm.getProductPrice());
        parseProductPrice(planInfo, productPrice);
        parseRouteListText(planInfo, productInfoUpdateForm.getRouteList());
        planInfo.setStock(NumberUtils.toInt(productInfoUpdateForm.getProductStock()));
        planInfo.setPlanTimeValue(NumberUtils.toLong(productInfoUpdateForm.getProductTimeValue()));

        productInfoRepository.updateById(planInfo);
    }

    public void deleteProduct(Long id) {
        productInfoRepository.deleteById(id);
    }

    public PlanInfo getProduct(Long id) {
        return productInfoRepository.selectById(id);
    }

}
