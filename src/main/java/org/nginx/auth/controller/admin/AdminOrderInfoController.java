package org.nginx.auth.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.nginx.auth.constant.OrderInfoConstant;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.OrderDetailVO;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.OrderRefundInfo;
import org.nginx.auth.model.User;
import org.nginx.auth.service.*;
import org.nginx.auth.util.RedirectPageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderInfoController {

    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private AdminOrderInfoService adminOrderInfoService;
    @Autowired
    private OrderPaymentInfoService orderPaymentInfoService;
    @Autowired
    private OrderRefundInfoService orderRefundInfoService;
    @Autowired
    private RefundSupportService refundSupportService;

    @GetMapping("/index.html")
    public String orderListPage(HttpServletRequest request, Model model, Integer page, Integer size) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        BasicPaginationVO<OrderInfo> orderPage = adminOrderInfoService.orderListPage(page, size);
        model.addAttribute("pagination", orderPage);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "/admin/order/index.html";
    }

    @GetMapping("/detail.html")
    public String selectOrderDetail(String orderId, Model model) {

        model.addAttribute("orderId", orderId);

        OrderDetailVO detail = adminOrderInfoService.getOrderDetail(orderId);
        model.addAttribute("orderDetailVO", detail);

        // 查询用户信息
        User user = adminUserService.selectById(detail.getOrderInfo().getUserId());
        model.addAttribute("user", user);

        // 查询支付信息
        List<OrderPaymentInfo> paymentList = orderPaymentInfoService.selectListByOrderId(orderId);
        model.addAttribute("paymentList", paymentList);

        // 查询退款历史
        List<OrderRefundInfo> refundHistory = orderRefundInfoService.selectListByOrderId(orderId);
        model.addAttribute("refundHistory", refundHistory);

        // 是否展示退款按钮
        boolean refundBtn = OrderInfoConstant.AVAILABLE_REFUND_TRADE_STATUS
                .contains(detail.getOrderInfo().getOrderStatus());
        model.addAttribute("refundBtn", refundBtn);

        return "/admin/order/detail.html";
    }


    /**
     * 管理员主动退款
     *
     * @param orderId
     * @param refundAmount
     * @param refundReason
     * @param refundPurchase
     * @param model
     * @return
     */
    @PostMapping("/refund.html")
    public String orderRefundPage(@RequestParam String orderId,
                                  @RequestParam String refundAmount,
                                  @RequestParam String refundReason,
                                  @RequestParam Boolean refundPurchase,
                                  Model model) {
        Long refundAmountLong = new BigDecimal(refundAmount)
                .multiply(new BigDecimal(100))
                .longValue();

        try {
            orderRefundInfoService.refundByAdmin(orderId, refundAmountLong, refundReason, refundPurchase);
            model.addAttribute("message", "退款成功");
        } catch (Exception e) {
            model.addAttribute("error", "退款失败: " + e.getMessage());
        }
        // 退款后重定向回订单详情页
        return "redirect:/admin/order/detail.html?orderId=" + orderId;
    }

}
