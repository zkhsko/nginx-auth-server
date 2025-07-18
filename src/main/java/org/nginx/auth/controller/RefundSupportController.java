package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.RefundSupport;
import org.nginx.auth.service.OrderInfoService;
import org.nginx.auth.service.RefundSupportService;
import org.nginx.auth.util.RedirectPageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/user/support/refund")
public class RefundSupportController {

    @Autowired
    private RefundSupportService refundSupportService;
    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 退款申请列表页
     *
     * @param request
     * @param model
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = {"/", "/index.html"})
    public String index(HttpServletRequest request, Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        BasicPaginationVO<RefundSupport> refundPage = refundSupportService.refundSupportListPageByUserId(page, size);
        model.addAttribute("pagination", refundPage);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "/user/support/refund/index.html";
    }

    /**
     * 申请退款表单页面
     *
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping("/page.html")
    public String page(String orderId, Model model) {
        OrderInfo orderInfo = orderInfoService.selectByOrderId(orderId);
        model.addAttribute("order", orderInfo);
        model.addAttribute("orderId", orderId);
        return "/user/support/refund/page.html";
    }

    @PostMapping("/create.html")
    public String create(@RequestParam String orderId,
                         @RequestParam String refundAmount,
                         @RequestParam(required = false) Boolean refundPurchase,
                         @RequestParam String refundReason) {

        // 将退款金额转换为分
        Long refundAmountInCents = new BigDecimal(refundAmount)
                .multiply(BigDecimal.valueOf(100))
                .longValue();
        refundSupportService.createRefundSupport(orderId, refundAmountInCents, refundPurchase, refundReason);

        // 跳转到退款申请列表页面
        return "redirect:/user/support/refund/index.html";
    }

}
