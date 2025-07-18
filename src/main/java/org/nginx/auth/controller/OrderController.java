package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.dto.vo.OrderDetailVO;
import org.nginx.auth.model.OrderInfo;
import org.nginx.auth.model.RefundSupport;
import org.nginx.auth.model.User;
import org.nginx.auth.request.OrderCreateParam;
import org.nginx.auth.service.OrderInfoService;
import org.nginx.auth.service.RefundSupportService;
import org.nginx.auth.util.RedirectPageUtil;
import org.nginx.auth.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private RefundSupportService refundSupportService;

    @GetMapping(value = {"/", "/index.html"})
    public String index(HttpServletRequest request, Model model, Integer page, Integer size) {
        User user = SessionUtil.getCurrentUser();
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }

        BasicPaginationVO<OrderInfo> pagination = orderInfoService.orderListPage(user, page, size);

        model.addAttribute("pagination", pagination);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "/user/order/index.html";
    }

    @PostMapping("/create")
//    public OrderCreateDTO createOrder(@RequestBody List<OrderCreateParam> paramList) {
    public String createOrder(String skuId, Long cnt) {

//        String paymentChannel = param.getPaymentChannel();
//        boolean validEnum = EnumUtils.isValidEnum(PaymentChannelEnum.class, paymentChannel);
//        if (!validEnum) {
//            throw new IllegalArgumentException("支付渠道不合法");
//        }
//        PaymentChannelEnum paymentChannelEnum = EnumUtils.getEnum(PaymentChannelEnum.class, paymentChannel);

        List<OrderCreateParam> paramList = new ArrayList<>();
        OrderCreateParam param = new OrderCreateParam();
        param.setSkuId(skuId);
        param.setCnt(cnt);
        paramList.add(param);


        String orderId = orderInfoService.createOrder(paramList);
        return "redirect:/user/order/detail.html?orderId=" + orderId;
    }

    @GetMapping("/detail.html")
    public String orderDetail(String orderId, Model model) {

        model.addAttribute("orderId", orderId);

        OrderDetailVO detail = orderInfoService.getOrderDetail(orderId);

        model.addAttribute("orderDetailVO", detail);

        return "/user/order/detail.html";
    }

    @GetMapping("/pay.html")
    public String orderDetail(String orderId, String paymentChannel, Model model) {
        String pay = orderInfoService.pay(orderId, paymentChannel);

        pay = "data:image/png;base64," + pay;

        model.addAttribute("pay", pay);

        return "/user/order/pay.html";
    }
}
