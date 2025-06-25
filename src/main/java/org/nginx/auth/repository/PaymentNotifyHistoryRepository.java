package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.PaymentNotifyHistory;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
@Mapper
public interface PaymentNotifyHistoryRepository extends BaseMapper<PaymentNotifyHistory> {

}
