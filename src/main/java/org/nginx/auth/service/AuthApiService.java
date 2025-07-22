package org.nginx.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nginx.auth.model.PremiumPlan;
import org.nginx.auth.model.PremiumPlanPredicate;
import org.nginx.auth.model.SubscriptionInfo;
import org.nginx.auth.model.User;
import org.nginx.auth.repository.PremiumPlanPredicateRepository;
import org.nginx.auth.repository.PremiumPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthApiService {

    @Autowired
    private SubscriptionInfoService subscriptionInfoService;
    @Autowired
    private PremiumPlanRepository premiumPlanRepository;
    @Autowired
    private PremiumPlanPredicateRepository premiumPlanPredicateRepository;

    public String access(HttpServletRequest request, HttpServletResponse response, User user) {

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "401 Authorization Required";
        }

        List<SubscriptionInfo> subscriptionInfoList = subscriptionInfoService.selectByUserId(user.getId());

        // 用户没有订阅信息
        if (subscriptionInfoList == null || subscriptionInfoList.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "403 Forbidden";
        }

        String serverName = request.getServerName();
        // 鉴权要请求的真实URI,不是鉴权本身的URL
        String requestURI = request.getHeader("X-Original-URI");

        for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {

            boolean active = calcSubActive(subscriptionInfo, request);
            if (active) {
                // 找到了一个满足的订阅
                return "success";
            }

        }

        // 最终没有找到满足的订阅信息
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "403 Forbidden";
    }

    /**
     * 判断当前订阅是不是对当前请求的url生效
     *
     * @return
     */
    private boolean calcSubActive(SubscriptionInfo subscriptionInfo, HttpServletRequest request) {
        long now = new Date().getTime();

        String serverName = request.getServerName();
        String requestURI = request.getHeader("X-Original-URI");

        if (subscriptionInfo.getSubscribeExpireTime() == null) {
            // 没有设置订阅到期时间
            return false;
        }

        // 检查订阅是否过期
        if (subscriptionInfo.getSubscribeExpireTime().getTime() < now) {
            // 过期了
            return false;
        }

        // 如果订阅没有过期,判断predicate是否满足
        Long premiumPlanId = subscriptionInfo.getPremiumPlanId();
        PremiumPlan premiumPlan = premiumPlanRepository.selectById(premiumPlanId);
        if (premiumPlan == null) {
            return false; // 如果没有找到对应的PremiumPlan,跳过
        }

        String predicateListText = premiumPlan.getPredicateListText();
        if (StringUtils.isBlank(predicateListText)) {
            // TODO 没有指定断言规则,暂时先不允许访问
            return false;
        }

        String[] predicateIdList = predicateListText.split(",");

        Set<Long> predicateIdSet = new HashSet<>();
        for (String predicateId : predicateIdList) {
            if (StringUtils.isBlank(predicateId)) {
                continue; // 路由列表中可能有空字符串,跳过
            }
            // 解析路由,获取对应的predicateId
            predicateIdSet.add(Long.parseLong(predicateId.trim()));
        }


        List<PremiumPlanPredicate> predicateList =
                premiumPlanPredicateRepository.selectByIds(predicateIdSet);

        if (CollectionUtils.isEmpty(predicateList)) {
            // 有指定的订阅断言但是没有查到对应的断言信息,
            // TODO 暂定为不允许访问
            return false;
        }

        for (PremiumPlanPredicate premiumPlanPredicate : predicateList) {
            // predicateList中的记录只要有一个满足就允许,记录之间是或的关系
            // predicateText每一行都必须同时满足.是且的关系
            String predicateText = premiumPlanPredicate.getPredicateText();

            boolean access = resolvePredicateText(predicateText, serverName, requestURI);
            if (access) {
                return true; // 如果有一个断言满足,则允许访问
            }


        }

        // 最终没有找到一个满足的断言
        return false;


    }

    private boolean resolvePredicateText(String predicateText, String serverName, String requestURI) {
        if (StringUtils.isBlank(predicateText)) {
            return false; // 如果predicateText为空,则不允许访问
        }

        AntPathMatcher matcher = new AntPathMatcher();
        String[] patternList = predicateText.split("\\n");
        for (String pattern : patternList) {
            if (StringUtils.isBlank(pattern)) {
                continue; // 跳过空行
            }

            String prefixHost = "Host=";
            String prefixPath = "Path=";

            if (StringUtils.startsWithIgnoreCase(pattern, prefixHost)) {
                pattern = StringUtils.removeStartIgnoreCase(pattern, prefixHost);
                if (!matcher.match(pattern, serverName)) {
                    return false; // 如果请求的Host不匹配,则不允许访问
                }
            } else if (StringUtils.startsWithIgnoreCase(pattern, prefixPath)) {
                pattern = StringUtils.removeStartIgnoreCase(pattern, prefixPath);
                if (!matcher.match(pattern, requestURI)) {
                    return false; // 如果请求的URI不匹配,则不允许访问
                }
            } else {
                // 其他开头的断言暂时什么都不做
            }
        }

        return true; // 如果所有断言都满足,则允许访问
    }


}
