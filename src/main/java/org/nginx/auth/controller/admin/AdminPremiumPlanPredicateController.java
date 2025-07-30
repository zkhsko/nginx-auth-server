package org.nginx.auth.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.nginx.auth.dto.form.AdminPremiumPlanPredicateCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanPredicateUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PremiumPlanPredicate;
import org.nginx.auth.service.AdminPremiumPlanPredicateService;
import org.nginx.auth.util.BeanCopyUtil;
import org.nginx.auth.util.RedirectPageUtil;
import org.nginx.auth.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/admin/premium-plan-predicate")
public class AdminPremiumPlanPredicateController {

    @Autowired
    private AdminPremiumPlanPredicateService adminPremiumPlanPredicateService;

    @GetMapping("/index.html")
    public String predicateListPage(HttpServletRequest request, Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        BasicPaginationVO<PremiumPlanPredicate> predicatePageVO = adminPremiumPlanPredicateService.predicateListPage(page, size);
        model.addAttribute("pagination", predicatePageVO);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "admin/predicate/index";
    }

    @GetMapping("/{version}/create.html")
    public String createPredicatePage(@PathVariable String version, Model model) {
        model.addAttribute("form", new AdminPremiumPlanPredicateCreateForm());
        return "admin/predicate/" + version + "/create";
    }

    @PostMapping("/{version}/create.html")
    public String createPredicateAction(@PathVariable String version, Model model, AdminPremiumPlanPredicateCreateForm createForm) {
        Map<String, String> validateRtn = ValidatorUtil.validate(createForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", createForm);
            return "admin/predicate/" + version + "/create";
        }

        adminPremiumPlanPredicateService.createPredicate(createForm);
        return "redirect:/admin/premium-plan-predicate/index.html";
    }

    @PostMapping("/delete.html")
    public String deletePredicateAction(@RequestParam Long id, @RequestParam String redirect) {
        adminPremiumPlanPredicateService.deletePredicate(id);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @GetMapping("/{version}/update.html")
    public String updatePredicatePage(@PathVariable String version, Model model, @RequestParam Long id, @RequestParam String redirect) {
        PremiumPlanPredicate predicate = adminPremiumPlanPredicateService.getPredicate(id);
        if (predicate == null) {
            return "redirect:/admin/premium-plan-predicate/index.html";
        }

        AdminPremiumPlanPredicateUpdateForm updateForm = new AdminPremiumPlanPredicateUpdateForm();
        BeanCopyUtil.copy(predicate, updateForm);

        model.addAttribute("form", updateForm);
        model.addAttribute("redirect", redirect);

        return "admin/predicate/" + version + "/update";
    }

    @PostMapping("/{version}/update.html")
    public String updatePredicateAction(@PathVariable String version, @RequestParam Long id, @RequestParam String redirect, Model model, AdminPremiumPlanPredicateUpdateForm updateForm) {
        updateForm.setId(id);

        Map<String, String> validateRtn = ValidatorUtil.validate(updateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", updateForm);
            model.addAttribute("redirect", redirect);
            return "admin/predicate/" + version + "/update";
        }

        adminPremiumPlanPredicateService.updatePredicate(updateForm);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }
}
