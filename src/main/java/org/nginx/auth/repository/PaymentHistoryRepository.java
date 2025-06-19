package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.PaymentHistory;

import java.util.List;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
@Mapper
public interface PaymentHistoryRepository extends BaseMapper<PaymentHistory> {

    List<PaymentHistory> selectListByAccountId(Long accountId);

    int updateOrderPayInfo(PaymentHistory paymentHistory);

}
