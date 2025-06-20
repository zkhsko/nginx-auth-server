package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.PremiumPlan;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
// 文件已重命名为PremiumPlanRepository.java，接口已调整为PremiumPlan相关内容
@Mapper
public interface PremiumPlanRepository extends BaseMapper<PremiumPlan> {

//    List<ProductInfo> selectByIdList(Collection<Long> ids);

//    List<ProductInfo> selectAllInUse();
//
//    ProductInfo selectById(Long id);

    /**
     * 减库存
     */
    int reduceStock(Long id);

}
