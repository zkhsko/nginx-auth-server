package org.nginx.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.PlanInfo;
import org.nginx.auth.service.ProductInfoService;

/**
 * 匿名用户可以访问的接口
 */
@Controller
@RequestMapping("/anonymous")
public class AnonymousController {
    private static final Logger logger = LoggerFactory.getLogger(AnonymousController.class);

    @Autowired
    private ProductInfoService productInfoService;

    @GetMapping("/product/index.html")
    public String productListPage(Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        BasicPaginationVO<PlanInfo> productInfoPageVO = productInfoService.productListPage(page, size);
        model.addAttribute("pagination", productInfoPageVO);


        return "/anonymous/product/index.html";
    }

}
