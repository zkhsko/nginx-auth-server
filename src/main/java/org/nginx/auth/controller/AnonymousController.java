package org.nginx.auth.controller;

import org.nginx.auth.dto.vo.PremiumPlanVO;
import org.nginx.auth.model.PremiumPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.service.PremiumPlanService;

/**
 * 匿名用户可以访问的接口
 */
@Controller
@RequestMapping("/anonymous")
public class AnonymousController {
    private static final Logger logger = LoggerFactory.getLogger(AnonymousController.class);

    @Autowired
    private PremiumPlanService premiumPlanService;

    @GetMapping("/premium-plan/index.html")
    public String premiumPlanListPage(Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 12;
        }

        BasicPaginationVO<PremiumPlanVO> premiumPlanPageVO = premiumPlanService.premiumPlanListPage(page, size);
        model.addAttribute("pagination", premiumPlanPageVO);


        return "/anonymous/premium_plan/index.html";
    }

}
