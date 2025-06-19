package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.PlanInfo;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
@Mapper
public interface ProductInfoRepository extends BaseMapper<PlanInfo> {

//    List<ProductInfo> selectByIdList(Collection<Long> ids);

//    List<ProductInfo> selectAllInUse();
//
//    ProductInfo selectById(Long id);

    /**
     * 减库存
     */
    int reduceStock(Long id);

}
