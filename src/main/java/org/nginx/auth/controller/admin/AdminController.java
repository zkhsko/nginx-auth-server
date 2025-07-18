package org.nginx.auth.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageParam;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.nginx.auth.constant.OrderInfoConstant;
import org.nginx.auth.dto.form.AdminPremiumPlanCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.OrderDetailVO;
import org.nginx.auth.model.*;
import org.nginx.auth.service.*;
import org.nginx.auth.util.BeanCopyUtil;
import org.nginx.auth.util.RedirectPageUtil;
import org.nginx.auth.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * @author dongpo.li
 * @date 2024/12/28 00:27
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminPremiumPlanService adminPremiumPlanService;
    @Autowired
    private AdminUserService adminUserService;

    @RequestMapping(value = {"", "/"})
    public String index() {
        return "/admin/index.html";
    }

    // -- premium plan --

    @GetMapping("/premium-plan/index.html")
    public String premiumPlanListPage(HttpServletRequest request, Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        BasicPaginationVO<PremiumPlan> premiumPlanPageVO = adminPremiumPlanService.premiumPlanListPage(page, size);
        model.addAttribute("pagination", premiumPlanPageVO);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "/admin/premium_plan/index.html";
    }

    @GetMapping("/premium-plan/{version}/create.html")
    public String createPremiumPlanPage(@PathVariable String version, Model model) {

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);

        AdminPremiumPlanCreateForm premiumPlanCreateForm = new AdminPremiumPlanCreateForm();
        premiumPlanCreateForm.setPremiumPlanTimeUnit("DAY");
        model.addAttribute("form", premiumPlanCreateForm);

        return "/admin/premium_plan/" + version + "/create.html";
    }

    @PostMapping("/premium-plan/{version}/create.html")
    public String createPremiumPlanAction(@PathVariable String version, Model model,
                                          AdminPremiumPlanCreateForm premiumPlanCreateForm) {

        if (premiumPlanCreateForm.getInUse() == null) {
            premiumPlanCreateForm.setInUse(false);
        }

        Map<String, String> validateRtn = ValidatorUtil.validate(premiumPlanCreateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", premiumPlanCreateForm);

            PageParam pageParam = new PageParam(null, null, "id desc");
            PageHelper.startPage(pageParam);

            return "/admin/premium_plan/" + version + "/create.html";
        }

        adminPremiumPlanService.createPremiumPlan(premiumPlanCreateForm);
        return "redirect:/admin/premium-plan/index.html";
    }

    @PostMapping("/premium-plan/delete.html")
    public String deletePremiumPlanAction(HttpServletRequest request, @RequestParam Long id, @RequestParam String redirect) {
        adminPremiumPlanService.deletePremiumPlan(id);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @GetMapping("/premium-plan/{version}/update.html")
    public String updatePremiumPlanPage(HttpServletRequest request, @PathVariable String version, Model model,
                                        @RequestParam Long id, @RequestParam String redirect) {
        PremiumPlan premiumPlan = adminPremiumPlanService.getPremiumPlan(id);

        model.addAttribute("redirect", redirect);

        if (premiumPlan == null) {
            return "redirect:/admin/premium-plan/index.html";
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
//        List<RouteInfo> routeInfoList = routeInfoRepository.selectList(null);
//        model.addAttribute("routeList", routeInfoList);

        AdminPremiumPlanUpdateForm premiumPlanUpdateForm = new AdminPremiumPlanUpdateForm();
        BeanCopyUtil.copy(premiumPlan, premiumPlanUpdateForm);
        BigDecimal premiumPlanPrice = new BigDecimal(premiumPlan.getPremiumPlanPrice())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        premiumPlanUpdateForm.setPremiumPlanPrice(premiumPlanPrice);
//        String routeListText = premiumPlan.getRouteListText();
//        if (routeListText != null) {
//            String[] routeList = routeListText.split(",");
//            LinkedHashSet<Long> routeListSet = new LinkedHashSet<>();
//            for (String routeId : routeList) {
//                routeListSet.add(Long.valueOf(routeId));
//            }
//            premiumPlanUpdateForm.setRouteList(routeListSet);
//        } else {
//            premiumPlanUpdateForm.setRouteList(new LinkedHashSet<>());
//        }
        premiumPlanUpdateForm.setPremiumPlanStock(premiumPlan.getPremiumPlanStock());
        premiumPlanUpdateForm.setPremiumPlanTimeValue(premiumPlan.getPremiumPlanTimeValue());
        model.addAttribute("form", premiumPlanUpdateForm);

        return "/admin/premium_plan/" + version + "/update.html";
    }

    @PostMapping("/premium-plan/{version}/update.html")
    public String updatePremiumPlanAction(HttpServletRequest request, @PathVariable String version,
                                          @RequestParam Long id, @RequestParam String redirect,
                                          Model model, AdminPremiumPlanUpdateForm premiumPlanUpdateForm) {
        premiumPlanUpdateForm.setId(id);
        if (premiumPlanUpdateForm.getInUse() == null) {
            premiumPlanUpdateForm.setInUse(false);
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
//        List<RouteInfo> routeInfoList = routeInfoRepository.selectList(null);
//        model.addAttribute("routeList", routeInfoList);

        Map<String, String> validateRtn = ValidatorUtil.validate(premiumPlanUpdateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", premiumPlanUpdateForm);
            model.addAttribute("redirect", redirect);
            return "/admin/premium_plan/" + version + "/update.html";
        }

        adminPremiumPlanService.updatePremiumPlan(premiumPlanUpdateForm);
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

        BasicPaginationVO<User> userInfoPageVO = adminUserService.userListPage(page, size);
        model.addAttribute("pagination", userInfoPageVO);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "/admin/user/index.html";
    }

    // block
    @PostMapping("/user/block.html")
    public String blockAccountAction(@RequestParam Long id, @RequestParam String redirect) {
        adminUserService.changeUserBlock(id, true);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @PostMapping("/user/unblock.html")
    public String unblockAccountAction(@RequestParam Long id, @RequestParam String redirect) {
        adminUserService.changeUserBlock(id, false);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

}
