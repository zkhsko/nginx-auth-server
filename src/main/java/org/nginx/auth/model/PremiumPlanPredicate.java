package org.nginx.auth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("premium_plan_predicate")
public class PremiumPlanPredicate extends AutoIncrementBaseEntity {

    /**
     * 展示用的名称
     */
    @TableField("predicate_name")
    private String predicateName;

    /**
     * 具体的断言内容
     * 例如：Host=**.domain.com
     * Path=/public/**
     * 等
     * 目前只支持Host和Path两种类型的断言
     * 两个可以同时存在,同时存在的话,换行符分隔,表示需要两个条件同时满足
     * Path配置的是ant风格的pattern
     */
    private String predicateText;

    /**
     * predicateText解析的版本号,给后续不兼容的升级提供可能,暂时没有用
     */
    private String predicateVersion;

    public String getPredicateName() {
        return predicateName;
    }

    public void setPredicateName(String predicateName) {
        this.predicateName = predicateName;
    }

    public String getPredicateText() {
        return predicateText;
    }

    public void setPredicateText(String predicateText) {
        this.predicateText = predicateText;
    }

    public String getPredicateVersion() {
        return predicateVersion;
    }

    public void setPredicateVersion(String predicateVersion) {
        this.predicateVersion = predicateVersion;
    }
}
