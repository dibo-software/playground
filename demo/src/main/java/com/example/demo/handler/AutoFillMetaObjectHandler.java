package com.example.demo.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.diboot.core.config.Cons;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.util.IamSecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
* 字段自动填充策略处理类
* @author MyName
* @version 1.0
* @date 2022-12-30
* Copyright © MyCompany
*/
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler{
		
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建数据时，创建时间与更新时间都由数据库进行自动填充
        this.strictInsertFill(metaObject, Cons.FieldName.createBy.name(), IamSecurityUtils::getCurrentUserId, Long.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
    		this.strictUpdateFill(metaObject, Cons.FieldName.updateTime.name(), Date::new, Date.class);
        this.strictUpdateFill(metaObject, Cons.FieldName.updateBy.name(), IamSecurityUtils::getCurrentUserId, Long.class);
    }
}