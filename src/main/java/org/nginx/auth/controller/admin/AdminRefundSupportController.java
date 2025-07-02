package org.nginx.auth.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.RefundSupport;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.OrderPaymentInfoRepository;
import org.nginx.auth.repository.RefundSupportRepository;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.service.OrderInfoService;
import org.nginx.auth.service.OrderPaymentInfoService;
import org.nginx.auth.service.RefundSupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/support/refund")
public class AdminRefundSupportController {

    @Autowired
    private RefundSupportService refundSupportService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderPaymentInfoService orderPaymentInfoService;

    @RequestMapping(value = {"/", "/index.html"})
    public String index() {
        return "/admin/support/refund/index.html";
    }

    @RequestMapping("/detail.html")
    public String detail(@RequestParam("id") String id, Model model) {
        RefundSupport refundSupport = refundSupportService.selectByRefundSupportId(id);
        if (refundSupport == null) {
            // TODO: 这样处理非法退款单是不是合适?
            return "redirect:/admin/support/refund/index.html";
        }
        model.addAttribute("refundSupport", refundSupport);
        model.addAttribute("orderDetail", orderInfoService.getOrderDetail(refundSupport.getOrderId()));
        User user = userRepository.selectById(refundSupport.getUserId());
        model.addAttribute("user", user);
        List<OrderPaymentInfo> paymentList = orderPaymentInfoService.selectListByOrderId(refundSupport.getOrderId());
        model.addAttribute("paymentList", paymentList);
        return "/admin/support/refund/detail.html";
    }

}
