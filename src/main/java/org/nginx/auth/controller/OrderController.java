package org.nginx.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.constant.BasicConstant;
import org.nginx.auth.dto.bo.OrderCreateResponseBO;
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
import org.nginx.auth.dto.vo.PremiumPlanSkpVO;
import org.nginx.auth.service.PremiumPlanService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    private PremiumPlanService premiumPlanService;

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

        return "user/order/index";
    }

    @GetMapping("/confirm.html")
    public String confirmOrder(Long skpId, @RequestParam(required = false) Long skuId, Model model) {
        PremiumPlanSkpVO premiumPlanSkpVO = premiumPlanService.selectPremiumPlanSkp(skpId);
        model.addAttribute("skp", premiumPlanSkpVO);
        model.addAttribute("selectedSkuId", skuId);
        return "user/order/confirm";
    }

    /**
     * 下单接口
     * 如果没有传accessKey,表示用户新买,需要先新建账号
     * 如果传了,表示续费,要先看之前买的和当前续费的是不是同一个商品
     *
     * @param skuId
     * @param cnt
     * @param accessKey
     * @return
     */
    @PostMapping("/create")
//    public OrderCreateDTO createOrder(@RequestBody List<OrderCreateParam> paramList) {
    public String createOrder(String skuId, Long cnt, @RequestParam(required = false) String accessKey) {

        List<OrderCreateParam> paramList = new ArrayList<>();
        OrderCreateParam param = new OrderCreateParam();
        param.setSkuId(skuId);
        param.setCnt(cnt);
        paramList.add(param);


        OrderCreateResponseBO responseBO = orderInfoService.createOrder(accessKey, paramList);

        String orderId = responseBO.getOrderId();
        if (StringUtils.isBlank(orderId)) {
            // 订单创建失败
            String errMsg = responseBO.getErrMsg();
            if (StringUtils.isBlank(errMsg)) {
                errMsg = "下单失败,请稍后再试";
            }
            // TODO: 这个错误页面要是一个单独的最好
            return "redirect:/user/order/index.html?error=" + errMsg;
        }

        String accessKeyQueryString = "";
        if (StringUtils.isNotBlank(responseBO.getAccessKey())) {
            accessKeyQueryString = "&accessKey=" + responseBO.getAccessKey();
        }
        return "redirect:/user/order/detail.html?orderId=" + orderId + accessKeyQueryString;
    }

    @GetMapping("/detail.html")
    public String orderDetailPage(String orderId, String accessKey, Model model) {

        // accessKey一般不存在也不需要,只有在下单的新建账户了,传过来给用户展示一下创建的用户
        if (StringUtils.isNotBlank(accessKey)) {
            model.addAttribute("accessKey", accessKey);
        }

        model.addAttribute("orderId", orderId);

        OrderDetailVO detail = orderInfoService.getOrderDetail(orderId);

        model.addAttribute("orderDetailVO", detail);

        return "user/order/detail";
    }

    @GetMapping("/pay.html")
    public String pay(String orderId, String paymentChannel, Model model) {
        String pay = orderInfoService.pay(orderId, paymentChannel);

        pay = "data:image/png;base64," + pay;

        model.addAttribute("pay", pay);

        return "user/order/pay";
    }
}
