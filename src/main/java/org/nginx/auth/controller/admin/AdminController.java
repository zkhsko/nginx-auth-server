package org.nginx.auth.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageParam;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.nginx.auth.dto.form.AdminProductInfoCreateForm;
import org.nginx.auth.dto.form.AdminProductInfoUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PlanInfo;
import org.nginx.auth.model.User;
import org.nginx.auth.service.AdminAccountService;
import org.nginx.auth.service.AdminProductService;
import org.nginx.auth.util.BeanCopyUtil;
import org.nginx.auth.util.RedirectPageUtil;
import org.nginx.auth.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author dongpo.li
 * @date 2024/12/28 00:27
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminProductService adminProductService;
    @Autowired
    private AdminAccountService adminAccountService;

    @RequestMapping(value = {"", "/"})
    public String index() {
        return "/admin/index.html";
    }

    // -- product --

    @GetMapping("/product/index.html")
    public String productListPage(HttpServletRequest request, Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        BasicPaginationVO<PlanInfo> productInfoPageVO = adminProductService.productListPage(page, size);
        model.addAttribute("pagination", productInfoPageVO);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "/admin/product/index.html";
    }

    @GetMapping("/product/{version}/create.html")
    public String createProductPage(@PathVariable String version, Model model) {

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);

        AdminProductInfoCreateForm productInfoCreateForm = new AdminProductInfoCreateForm();
        productInfoCreateForm.setProductTimeUnit("DAY");
        model.addAttribute("form", productInfoCreateForm);

        return "/admin/product/" + version + "/create.html";
    }

    @PostMapping("/product/{version}/create.html")
    public String createProductAction(@PathVariable String version, Model model,
                                      AdminProductInfoCreateForm productInfoCreateForm) {

        if (productInfoCreateForm.getInUse() == null) {
            productInfoCreateForm.setInUse(false);
        }

        Map<String, String> validateRtn = ValidatorUtil.validate(productInfoCreateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", productInfoCreateForm);

            PageParam pageParam = new PageParam(null, null, "id desc");
            PageHelper.startPage(pageParam);

            return "/admin/product/" + version + "/create.html";
        }

        adminProductService.createProduct(productInfoCreateForm);
        return "redirect:/admin/product/index.html";
    }

    @PostMapping("/product/delete.html")
    public String deleteProductAction(HttpServletRequest request, @RequestParam Long id, @RequestParam String redirect) {
        adminProductService.deleteProduct(id);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @GetMapping("/product/{version}/update.html")
    public String updateProductPage(HttpServletRequest request, @PathVariable String version, Model model,
                                    @RequestParam Long id, @RequestParam String redirect) {
        PlanInfo planInfo = adminProductService.getProduct(id);

        model.addAttribute("redirect", redirect);

        if (planInfo == null) {
            return "redirect:/admin/product/index.html";
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
//        List<RouteInfo> routeInfoList = routeInfoRepository.selectList(null);
//        model.addAttribute("routeList", routeInfoList);

        AdminProductInfoUpdateForm productInfoUpdateForm = new AdminProductInfoUpdateForm();
        BeanCopyUtil.copy(planInfo, productInfoUpdateForm);
        String productPrice = new BigDecimal(planInfo.getPrice())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                .toEngineeringString();
        productInfoUpdateForm.setProductPrice(productPrice);
        String routeListText = planInfo.getRouteListText();
        if (routeListText != null) {
            String[] routeList = routeListText.split(",");
            LinkedHashSet<Long> routeListSet = new LinkedHashSet<>();
            for (String routeId : routeList) {
                routeListSet.add(Long.valueOf(routeId));
            }
            productInfoUpdateForm.setRouteList(routeListSet);
        } else {
            productInfoUpdateForm.setRouteList(new LinkedHashSet<>());
        }
        productInfoUpdateForm.setProductStock(planInfo.getStock().toString());
        productInfoUpdateForm.setProductTimeValue(planInfo.getPlanTimeValue().toString());
        model.addAttribute("form", productInfoUpdateForm);

        return "/admin/product/" + version + "/update.html";
    }

    @PostMapping("/product/{version}/update.html")
    public String updateProductAction(HttpServletRequest request, @PathVariable String version,
                                      @RequestParam Long id, @RequestParam String redirect,
                                      Model model, AdminProductInfoUpdateForm productInfoUpdateForm) {
        productInfoUpdateForm.setId(id);
        if (productInfoUpdateForm.getInUse() == null) {
            productInfoUpdateForm.setInUse(false);
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
//        List<RouteInfo> routeInfoList = routeInfoRepository.selectList(null);
//        model.addAttribute("routeList", routeInfoList);

        Map<String, String> validateRtn = ValidatorUtil.validate(productInfoUpdateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", productInfoUpdateForm);
            model.addAttribute("redirect", redirect);
            return "/admin/product/" + version + "/update.html";
        }

        adminProductService.updateProduct(productInfoUpdateForm);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    // -- user --
    @GetMapping("/user/index.html")
    public String userListPage(HttpServletRequest request, Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        BasicPaginationVO<User> userInfoPageVO = adminAccountService.userListPage(page, size);
        model.addAttribute("pagination", userInfoPageVO);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "/admin/user/index.html";
    }

    // block
    @PostMapping("/user/block.html")
    public String blockAccountAction(@RequestParam Long id, @RequestParam String redirect) {
        adminAccountService.changeUserBlock(id, true);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @PostMapping("/user/unblock.html")
    public String unblockAccountAction(@RequestParam Long id, @RequestParam String redirect) {
        adminAccountService.changeUserBlock(id, false);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }



    // -- order --
    @GetMapping("/order/index.html")
    public String orderListPage() {
        return "";
    }

    @PostMapping("/order/refund.html")
    public String orderRefundPage() {
        return "";
    }

}
