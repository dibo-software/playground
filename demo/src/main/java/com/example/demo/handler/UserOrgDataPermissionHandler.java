package com.example.demo.handler;

import com.diboot.core.config.Cons;
import com.diboot.iam.data.UserOrgDataAccessScopeManager;
import org.springframework.stereotype.Component;

/**
 * 拦截处理器默认实现 - 基于用户组织的实现策略
 * @author JerryMa
 * @version v3.0.0
 * @date 2022/9/9
 * Copyright © diboot.com
 */
// title = "基于用户组织的权限控制"
@Component
public class UserOrgDataPermissionHandler extends UserOrgDataAccessScopeManager {

    protected boolean isOrgFieldName(String fieldName) {
        return super.isOrgFieldName(fieldName) || "reportOrg".equals(fieldName);
    }

}