package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.PremiumPlanSku;

/**
 * @author dongpo.li
 * @date 2024/08/01
 */
@Mapper
public interface PremiumPlanSkuRepository extends BaseMapper<PremiumPlanSku> {

    /**
     * 减库存
     */
    int reduceStock(Long id);

}