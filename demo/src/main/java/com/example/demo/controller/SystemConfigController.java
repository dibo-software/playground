package com.example.demo.controller;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.S;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.config.SystemConfigType;
import com.diboot.iam.entity.SystemConfig;
import com.diboot.iam.service.SystemConfigService;
import com.diboot.iam.vo.SystemConfigVO;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 系统配置Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@BindPermission(name = "系统配置")
@RestController
@RequestMapping("/systemConfig")
public class SystemConfigController extends BaseCrudRestController<SystemConfig> {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取系统配置类型列表
     *
     * @return
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping("/typeList")
    public JsonResult<List<LabelValue>> getTypeListMapping() {
        return JsonResult.OK(systemConfigService.getTypeList());
    }

    /**
     * 获取指定类型的系统配置信息
     *
     * @param type 类型
     * @return
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{type}")
    public JsonResult<List<SystemConfigVO>> getConfigByTypeMapping(@PathVariable String type) {
        return JsonResult.OK(systemConfigService.getConfigByType(type));
    }

    /**
     * 更新系统配置
     *
     * @param systemConfig
     * @return
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult<?> updateConfigMapping(@RequestBody SystemConfig systemConfig) {
        return new JsonResult<>(systemConfigService.createOrUpdateEntity(systemConfig));
    }

    /**
     * 重置指定类型或属性的系统配置
     *
     * @param type 类型
     * @param prop 属性（为空重置整个类型）
     * @return
     */
    @Log(operation = "重置")
    @BindPermission(name = "重置", code = OperationCons.CODE_WRITE)
    @DeleteMapping({"/{type}/{prop}", "/{type}"})
    public JsonResult<?> deleteTypeMapping(@PathVariable String type, @PathVariable(required = false) String prop) {
        systemConfigService.deleteByTypeAndProp(type, prop);
        return JsonResult.OK();
    }

    /**
     * 系统配置测试
     *
     * @param type 类型
     * @param data 数据
     * @return
     */
    @Log(operation = "测试")
    @BindPermission(name = "测试", code = OperationCons.CODE_WRITE)
    @PostMapping("/{type}")
    public JsonResult<?> configTestMapping(@PathVariable String type, @RequestBody Map<String, Object> data) {
        systemConfigService.configTest(type, data);
        return JsonResult.OK();
    }
    
    /**
     * 获取配置值 用于前端获取配置值
     *
     * @param type 配置类型
     * @param prop 属性（为空重置整个类型）
     * @return 配置值
     */
    @Log(operation = "获取配置值")
    @GetMapping({"/value/{type}/{prop}", "/value/{type}"})
    public JsonResult<?> getConfigMapping(@PathVariable String type, @PathVariable(required = false) String prop) {

        List<Enum<? extends SystemConfigType>> configTypeItems = systemConfigService.getConfigItemsMap().getOrDefault(type, Collections.emptyList());
        if (configTypeItems.isEmpty()) {
            return JsonResult.FAIL_VALIDATION("配置类型：" + type + " 不存在");
        }
        if (S.isEmpty(prop)) {
            return JsonResult.OK(SystemConfigType.values(configTypeItems.get(0).getClass()).getMap());
        }
        for (Enum<? extends SystemConfigType> configTypeItem : configTypeItems) {
            if (configTypeItem.name().equals(prop)) {
                return JsonResult.OK(((SystemConfigType) configTypeItem).getValue());
            }
        }
        return JsonResult.FAIL_VALIDATION("配置类型：" + type + " 属性：" + prop + " 不存在");
    }

}
