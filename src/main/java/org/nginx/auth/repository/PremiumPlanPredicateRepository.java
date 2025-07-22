package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.PremiumPlanPredicate;

@Mapper
public interface PremiumPlanPredicateRepository extends BaseMapper<PremiumPlanPredicate> {

}
