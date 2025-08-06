package org.nginx.auth.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageParam;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.dto.form.AdminPremiumPlanSkpCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanSkpUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PremiumPlanPredicate;
import org.nginx.auth.model.PremiumPlanSkp;
import org.nginx.auth.model.PremiumPlanSku;
import org.nginx.auth.repository.PremiumPlanPredicateRepository;
import org.nginx.auth.repository.PremiumPlanSkuRepository;
import org.nginx.auth.service.AdminPremiumPlanService;
import org.nginx.auth.util.BeanCopyUtil;
import org.nginx.auth.util.RedirectPageUtil;
import org.nginx.auth.util.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.nginx.auth.dto.form.AdminPremiumPlanSkuCreateForm;
import org.nginx.auth.repository.PremiumPlanSkpRepository;

@Controller
@RequestMapping("/admin/premium-plan")
public class AdminPremiumPlanController {

    @Autowired
    private AdminPremiumPlanService adminPremiumPlanService;
    @Autowired
    private PremiumPlanPredicateRepository premiumPlanPredicateRepository;
    @Autowired
    private PremiumPlanSkuRepository premiumPlanSkuRepository;
    @Autowired
    private PremiumPlanSkpRepository premiumPlanSkpRepository;

    @GetMapping("/index.html")
    public String premiumPlanSkpListPage(HttpServletRequest request, Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }

        BasicPaginationVO<PremiumPlanSkp> premiumPlanPageVO = adminPremiumPlanService.premiumPlanSkpListPage(page, size);
        model.addAttribute("pagination", premiumPlanPageVO);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "admin/premium-plan/index";
    }

    @GetMapping("/{version}/create-skp.html")
    public String createPremiumPlanSkpPage(@PathVariable String version, Model model) {

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
        List<PremiumPlanPredicate> predicateInfoList = premiumPlanPredicateRepository.selectList(null);
        model.addAttribute("predicateList", predicateInfoList);

        AdminPremiumPlanSkpCreateForm premiumPlanSkpCreateForm = new AdminPremiumPlanSkpCreateForm();
        model.addAttribute("form", premiumPlanSkpCreateForm);

        return "admin/premium-plan/" + version + "/create-skp";
    }

    @PostMapping("/{version}/create-skp.html")
    public String createPremiumPlanSkpAction(@PathVariable String version, Model model,
                                             AdminPremiumPlanSkpCreateForm premiumPlanSkpCreateForm) {

        if (premiumPlanSkpCreateForm.getInUse() == null) {
            premiumPlanSkpCreateForm.setInUse(false);
        }

        Map<String, String> validateRtn = ValidatorUtil.validate(premiumPlanSkpCreateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", premiumPlanSkpCreateForm);

            PageParam pageParam = new PageParam(null, null, "id desc");
            PageHelper.startPage(pageParam);
            List<PremiumPlanPredicate> predicateInfoList = premiumPlanPredicateRepository.selectList(null);
            model.addAttribute("predicateList", predicateInfoList);

            return "admin/premium-plan/" + version + "/create-skp";
        }

        adminPremiumPlanService.createPremiumPlanSkp(premiumPlanSkpCreateForm);
        return "redirect:/admin/premium-plan/index.html";
    }

    @PostMapping("/delete-skp.html")
    public String deletePremiumPlanSkpAction(HttpServletRequest request, @RequestParam Long id, @RequestParam String redirect) {
        adminPremiumPlanService.deletePremiumPlanSkp(id);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @GetMapping("/{version}/update-skp.html")
    public String updatePremiumPlanSkpPage(HttpServletRequest request, @PathVariable String version, Model model,
                                           @RequestParam Long id, @RequestParam String redirect) {
        PremiumPlanSkp premiumPlanSkp = adminPremiumPlanService.selectPremiumPlanSkp(id);

        model.addAttribute("redirect", redirect);

        if (premiumPlanSkp == null) {
            return "redirect:/admin/premium-plan/index.html";
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
        List<PremiumPlanPredicate> predicateInfoList = premiumPlanPredicateRepository.selectList(null);
        model.addAttribute("predicateList", predicateInfoList);

        AdminPremiumPlanSkpUpdateForm premiumPlanSkpUpdateForm = new AdminPremiumPlanSkpUpdateForm();
        BeanCopyUtil.copy(premiumPlanSkp, premiumPlanSkpUpdateForm);

        String predicateListText = premiumPlanSkp.getPredicateListText();
        if (StringUtils.isNotBlank(predicateListText)) {
            String[] predicateList = predicateListText.split(",");
            LinkedHashSet<Long> predicateListSet = new LinkedHashSet<>();
            for (String predicateId : predicateList) {
                predicateListSet.add(Long.valueOf(predicateId));
            }
            premiumPlanSkpUpdateForm.setPredicateList(predicateListSet);
        } else {
            premiumPlanSkpUpdateForm.setPredicateList(new LinkedHashSet<>());
        }
        model.addAttribute("form", premiumPlanSkpUpdateForm);

        return "admin/premium-plan/" + version + "/update-skp";
    }

    @PostMapping("/{version}/update-skp.html")
    public String updatePremiumPlanSkpAction(HttpServletRequest request, @PathVariable String version,
                                             @RequestParam Long id, @RequestParam String redirect,
                                             Model model, AdminPremiumPlanSkpUpdateForm updateForm) {
        updateForm.setId(id);
        if (updateForm.getInUse() == null) {
            updateForm.setInUse(false);
        }

        PageParam pageParam = new PageParam(null, null, "id desc");
        PageHelper.startPage(pageParam);
        List<PremiumPlanPredicate> predicateInfoList = premiumPlanPredicateRepository.selectList(null);
        model.addAttribute("predicateList", predicateInfoList);

        Map<String, String> validateRtn = ValidatorUtil.validate(updateForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", updateForm);
            return "admin/premium-plan/" + version + "/update-skp";
        }

        adminPremiumPlanService.updatePremiumPlanSkp(updateForm);
        return "redirect:" + RedirectPageUtil.resolveRedirectUrl(redirect);
    }

    @GetMapping("/{version}/detail-skp.html")
    public String selectPremiumPlanSkpDetailPage(HttpServletRequest request, @PathVariable String version,
                                                 @RequestParam Long id, Model model) {
        PremiumPlanSkp premiumPlanSkp = adminPremiumPlanService.selectPremiumPlanSkp(id);
        if (premiumPlanSkp == null) {
            return "redirect:/admin/premium-plan/index.html";
        }

        List<PremiumPlanSku> skuList = adminPremiumPlanService.selectPremiumPlanSkuListBySkpId(id);

        model.addAttribute("skp", premiumPlanSkp);
        model.addAttribute("skuList", skuList);

        return "admin/premium-plan/" + version + "/detail-skp";
    }

    @GetMapping("/{version}/create-sku.html")
    public String createPremiumPlanSkuPage(@PathVariable String version, Model model, @RequestParam Long skpId) {
        PremiumPlanSkp premiumPlanSkp = adminPremiumPlanService.selectPremiumPlanSkp(skpId);
        if (premiumPlanSkp == null) {
            return "redirect:/admin/premium-plan/index.html";
        }

        model.addAttribute("skp", premiumPlanSkp);
        model.addAttribute("form", new AdminPremiumPlanSkuCreateForm());

        return "admin/premium-plan/" + version + "/create-sku";
    }

    @PostMapping("/{version}/create-sku.html")
    public String createPremiumPlanSkuAction(@PathVariable String version, Model model,
                                             @RequestParam Long skpId,
                                             AdminPremiumPlanSkuCreateForm createForm) {
        PremiumPlanSkp premiumPlanSkp = premiumPlanSkpRepository.selectById(skpId);
        if (premiumPlanSkp == null) {
            return "redirect:/admin/premium-plan/index.html";
        }

        if (createForm.getInUse() == null) {
            createForm.setInUse(false);
        }

        Map<String, String> validateRtn = ValidatorUtil.validate(createForm);
        if (MapUtils.isNotEmpty(validateRtn)) {
            model.addAllAttributes(validateRtn);
            model.addAttribute("form", createForm);
            model.addAttribute("skp", premiumPlanSkp);
            return "admin/premium-plan/" + version + "/create-sku";
        }

        adminPremiumPlanService.createPremiumPlanSku(skpId, createForm);
        return "redirect:/admin/premium-plan/v1.0.0/detail-skp.html?id=" + skpId;
    }
}
