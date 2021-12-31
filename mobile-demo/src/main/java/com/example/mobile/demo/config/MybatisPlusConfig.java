package com.example.mobile.demo.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MybatisPlus 相关配置
 * @author www.dibo.ltd
 * @version v2.4.0
 * @date 2021/12/31
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 配置拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 数据权限拦截器
        //interceptor.addInnerInterceptor(new DataAccessControlInteceptor());
        return interceptor;
    }

}
