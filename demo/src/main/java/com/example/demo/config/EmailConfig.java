package com.example.demo.config;

import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.V;
import com.diboot.iam.config.SystemConfigTest;
import com.diboot.iam.config.SystemConfigType;

import java.util.HashSet;
import java.util.Set;

/**
 * Email 配置
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
public enum EmailConfig implements SystemConfigType, SystemConfigTest<EmailConfigTestData> {
    /**
     * 服务器主机（默认值可自动读取配置文件，及用冒号分隔指定当配置文件中未配置时的默认值 ）
     */
    host("主机", "${spring.mail.host:smtp.qq.com}", true) {
        /**
         * 定义配置选项
         */
        @Override
        public Set<String> options() {
            return new HashSet<String>(){{
                add("smtp.qq.com");
                add("smtp.136.com");
            }};
        }
    },
    /**
     * 用户名（默认值可自动读取配置文件, 配置文件中未配置时默认值为null）
     */
    username("用户名", "${spring.mail.username}", true),
    /**
     * 密码（直接指定默认值）
     */
    password("授权码", 123456, false);

    private final String propLabel;
    private final Object defaultValue;
    private final boolean required;

    EmailConfig( String propLabel, Object defaultValue, boolean required) {
        this.propLabel = propLabel;
        this.defaultValue = defaultValue;
        this.required = required;
    }

    @Override
    public String typeLabel() {
        return "邮箱配置";
    }

    @Override
    public String propLabel() {
        return propLabel;
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public boolean required() {
        return required;
    }


    /**
     * 测试方法
     *
     * @param data 测试数据
     */
    @Override
    public void test(EmailConfigTestData data) {
        String errMsg = V.validateBeanErrMsg(data);
        if (errMsg != null) {
            throw new BusinessException(errMsg);
        }
        // 发送邮件...
    }
}