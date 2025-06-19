package org.nginx.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductController {

    @GetMapping(value = {"/", "/index.html"})
    public String index() {
        return "/normal/product/index.html";
    }

}
