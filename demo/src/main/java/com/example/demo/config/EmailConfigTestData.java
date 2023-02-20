package com.example.demo.config;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * 测试数据
 */
@Data
public class EmailConfigTestData {
    /**
     * 接收者
     */
    @Email(message = "接收者邮箱不正确", regexp = "^[\\w-]+@[\\w-]+(\\.[\\w-]+)+$")
    @NotNull(message = "接收者不能为空")
    private String to;
    /**
     * 标题
     */
    @NotNull(message = "标题不能为空")
    private String title;
    /**
     * 内容
     */
    @NotNull(message = "内容不能为空")
    private String content;
}