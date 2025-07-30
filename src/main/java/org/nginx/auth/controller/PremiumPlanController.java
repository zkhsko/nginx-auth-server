package org.nginx.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/premium-plan")
public class PremiumPlanController {

    @GetMapping(value = {"/", "/index.html"})
    public String index() {
        return "normal/premium-plan/index";
    }

}
