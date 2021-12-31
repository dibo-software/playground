package com.example.mobile.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring 相关配置
 * @author www.dibo.ltd
 * @version v2.4.0
 * @date 2021/12/31
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages={"com.example"})
@MapperScan(basePackages={"com.example.mobile.demo.mapper"})
public class SpringMvcConfig {

}