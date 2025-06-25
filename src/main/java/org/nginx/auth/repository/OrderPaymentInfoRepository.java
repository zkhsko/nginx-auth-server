package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.OrderPaymentInfo;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
@Mapper
public interface OrderPaymentInfoRepository extends BaseMapper<OrderPaymentInfo> {

    List<OrderPaymentInfo> selectListByOrderId(String orderId);

    int updateOrderPayInfo(OrderPaymentInfo orderPaymentInfo);

}
