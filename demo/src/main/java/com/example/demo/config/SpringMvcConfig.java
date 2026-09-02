package com.example.demo.config;

import com.diboot.file.config.FileProperties;
import com.diboot.file.interceptor.DefaultFileAccessInterceptor;
import com.diboot.file.interceptor.FileAccessInterceptor;
import com.diboot.file.service.FileAccessAuthorizer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

/**
 * Spring 相关配置
 *
 * @author www.dibo.ltd
 * @version v1.0
 * @date 2020/10/23
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.example"})
@MapperScan(basePackages = {"com.example.demo.mapper"})
public class SpringMvcConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 文件访问拦截器默认实现，业务方可自定义Bean替换
     */
    @Bean
    @ConditionalOnMissingBean
    public FileAccessInterceptor fileAccessInterceptor(FileProperties fileProperties,
            ObjectProvider<FileAccessAuthorizer> fileAccessAuthorizers) {
        return new DefaultFileAccessInterceptor(fileProperties, fileAccessAuthorizers.orderedStream().toList());
    }
}