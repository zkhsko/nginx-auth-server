package org.nginx.auth.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageParam;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.constant.OrderInfoConstant;
import org.nginx.auth.dto.form.AdminPremiumPlanCreateForm;
import org.nginx.auth.dto.form.AdminPremiumPlanUpdateForm;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.OrderDetailVO;
import org.nginx.auth.model.*;
import org.nginx.auth.repository.PremiumPlanPredicateRepository;
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
import java.util.LinkedHashSet;
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
    private AdminUserService adminUserService;

    @RequestMapping(value = {"", "/"})
    public String index() {
        return "admin/index";
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

        return "admin/user/index";
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
