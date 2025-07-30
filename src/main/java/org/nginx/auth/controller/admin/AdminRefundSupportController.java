package org.nginx.auth.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.nginx.auth.dto.vo.BasicPaginationVO;
import org.nginx.auth.model.OrderPaymentInfo;
import org.nginx.auth.model.RefundSupport;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.UserRepository;
import org.nginx.auth.service.OrderInfoService;
import org.nginx.auth.service.OrderPaymentInfoService;
import org.nginx.auth.service.OrderRefundInfoService;
import org.nginx.auth.service.RefundSupportService;
import org.nginx.auth.util.RedirectPageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
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
    @Autowired
    private OrderRefundInfoService orderRefundInfoService;

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

        BasicPaginationVO<RefundSupport> refundPage = refundSupportService.refundSupportListPage(page, size);
        model.addAttribute("pagination", refundPage);
        model.addAttribute("redirect", RedirectPageUtil.buildRedirectUrl(request));

        return "admin/support/refund/index";
    }

    @RequestMapping("/detail.html")
    public String detail(@RequestParam("refundSupportId") String refundSupportId, Model model) {
        RefundSupport refundSupport = refundSupportService.selectByRefundSupportId(refundSupportId);
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
        model.addAttribute("refundHistory", orderRefundInfoService.selectListByOrderId(refundSupport.getOrderId()));
        return "admin/support/refund/detail";
    }

    /**
     * 处理退款申请,确认退款
     */
    @RequestMapping("/confirm.html")
    public String confirm(@RequestParam("refundSupportId") String refundSupportId,
                          @RequestParam("refundAmount") String refundAmount,
                          @RequestParam(name = "returnPurchase", required = false) Boolean returnPurchase,
                          @RequestParam("remarkText") String remarkText) {

        BigDecimal refundAmountDecimal = new BigDecimal(refundAmount)
                .multiply(BigDecimal.valueOf(100));

        RefundSupport refundSupport = refundSupportService.selectByRefundSupportId(refundSupportId);
        if (refundSupport == null) {
            return "redirect:/admin/support/refund/index.html";
        }

        refundSupportService.refundByRefundSupport(refundSupport, refundAmountDecimal.longValue(), returnPurchase, remarkText);

        return "redirect:/admin/support/refund/detail.html?refundSupportId=" + refundSupportId;
    }

}
