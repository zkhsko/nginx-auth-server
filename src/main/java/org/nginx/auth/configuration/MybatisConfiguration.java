package org.nginx.auth.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongpo.li
 * @date 2024/12/28 01:03
 */
@Configuration
@MapperScan("org.nginx.auth")
public class MybatisConfiguration {
}
