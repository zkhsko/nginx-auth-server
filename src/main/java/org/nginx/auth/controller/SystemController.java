package org.nginx.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author dongpo.li
 * @date 2024/12/27 20:17
 */
@Controller
@RequestMapping
public class SystemController {
    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @RequestMapping(value = {"/", "/index.html"})
    public String index() {
        return "redirect:/user/order/index.html";
    }

}
