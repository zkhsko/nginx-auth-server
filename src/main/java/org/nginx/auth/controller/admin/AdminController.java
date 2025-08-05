package org.nginx.auth.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.*;
import org.nginx.auth.service.*;
import org.nginx.auth.util.RedirectPageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
