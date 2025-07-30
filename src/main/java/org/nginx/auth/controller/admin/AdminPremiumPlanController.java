package org.nginx.auth.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageParam;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.dto.form.AdminPremiumPlanCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PremiumPlan;
import org.nginx.auth.model.PremiumPlanPredicate;
import org.nginx.auth.repository.PremiumPlanPredicateRepository;
import org.nginx.auth.service.AdminPremiumPlanService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/premium-plan")
public class AdminPremiumPlanController {

    @Autowired
    private AdminPremiumPlanService adminPremiumPlanService;
    @Autowired
    private PremiumPlanPredicateRepository premiumPlanPredicateRepository;

    @GetMapping("/index.html")
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

        return "admin/premium_plan/index";
    }

    @GetMapping("/{version}/create.html")
    public String createPremiumPlanPage(@PathVariable String version, Model model) {

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
        List<PremiumPlanPredicate> predicateInfoList = premiumPlanPredicateRepository.selectList(null);
        model.addAttribute("predicateList", predicateInfoList);

        AdminPremiumPlanCreateForm premiumPlanCreateForm = new AdminPremiumPlanCreateForm();
        premiumPlanCreateForm.setPremiumPlanTimeUnit("DAY");
        model.addAttribute("form", premiumPlanCreateForm);

        return "admin/premium_plan/" + version + "/create";
    }

    @PostMapping("/{version}/create.html")
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

            return "admin/premium_plan/" + version + "/create";
        }

        adminPremiumPlanService.createPremiumPlan(premiumPlanCreateForm);
        return "redirect:/admin/premium-plan/index.html";
    }

    @PostMapping("/delete.html")
    public String deletePremiumPlanAction(HttpServletRequest request, @RequestParam Long id, @RequestParam String redirect) {
        adminPremiumPlanService.deletePremiumPlan(id);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @GetMapping("/{version}/update.html")
    public String updatePremiumPlanPage(HttpServletRequest request, @PathVariable String version, Model model,
                                        @RequestParam Long id, @RequestParam String redirect) {
        PremiumPlan premiumPlan = adminPremiumPlanService.getPremiumPlan(id);

        model.addAttribute("redirect", redirect);

        if (premiumPlan == null) {
            return "redirect:/admin/premium-plan/index.html";
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
        List<PremiumPlanPredicate> predicateInfoList = premiumPlanPredicateRepository.selectList(null);
        model.addAttribute("predicateList", predicateInfoList);

        AdminPremiumPlanUpdateForm premiumPlanUpdateForm = new AdminPremiumPlanUpdateForm();
        BeanCopyUtil.copy(premiumPlan, premiumPlanUpdateForm);
        BigDecimal premiumPlanPrice = new BigDecimal(premiumPlan.getPremiumPlanPrice())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        premiumPlanUpdateForm.setPremiumPlanPrice(premiumPlanPrice);
        String predicateListText = premiumPlan.getPredicateListText();
        if (StringUtils.isNotBlank(predicateListText)) {
            String[] predicateList = predicateListText.split(",");
            LinkedHashSet<Long> predicateListSet = new LinkedHashSet<>();
            for (String predicateId : predicateList) {
                predicateListSet.add(Long.valueOf(predicateId));
            }
            premiumPlanUpdateForm.setPredicateList(predicateListSet);
        } else {
            premiumPlanUpdateForm.setPredicateList(new LinkedHashSet<>());
        }
        premiumPlanUpdateForm.setPremiumPlanStock(premiumPlan.getPremiumPlanStock());
        premiumPlanUpdateForm.setPremiumPlanTimeValue(premiumPlan.getPremiumPlanTimeValue());
        model.addAttribute("form", premiumPlanUpdateForm);

        return "admin/premium_plan/" + version + "/update";
    }

    @PostMapping("/{version}/update.html")
    public String updatePremiumPlanAction(HttpServletRequest request, @PathVariable String version,
                                          @RequestParam Long id, @RequestParam String redirect,
                                          Model model, AdminPremiumPlanUpdateForm premiumPlanUpdateForm) {
        premiumPlanUpdateForm.setId(id);
        if (premiumPlanUpdateForm.getInUse() == null) {
            premiumPlanUpdateForm.setInUse(false);
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
        List<PremiumPlanPredicate> predicateInfoList = premiumPlanPredicateRepository.selectList(null);
        model.addAttribute("predicateList", predicateInfoList);

        Map<String, String> validateRtn = ValidatorUtil.validate(premiumPlanUpdateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", premiumPlanUpdateForm);
            model.addAttribute("redirect", redirect);
            return "admin/premium_plan/" + version + "/update";
        }

        adminPremiumPlanService.updatePremiumPlan(premiumPlanUpdateForm);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

}
