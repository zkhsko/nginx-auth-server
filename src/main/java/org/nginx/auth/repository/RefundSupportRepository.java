package org.nginx.auth.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.nginx.auth.model.RefundSupport;

@Mapper
public interface RefundSupportRepository extends BaseMapper<RefundSupport> {

}