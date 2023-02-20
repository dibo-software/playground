package com.example.demo.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.entity.I18nConfig;
import com.diboot.core.entity.ValidList;
import com.diboot.core.service.I18nConfigService;
import com.diboot.core.util.V;
import com.diboot.core.vo.I18nConfigVO;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.example.demo.controller.BaseCustomCrudRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 文件记录 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/i18n-config")
@BindPermission(name = "国际化配置")
@ConditionalOnBean(I18nConfigService.class)
public class I18nConfigController extends BaseCustomCrudRestController<I18nConfig> {

    @Autowired
    private I18nConfigService i18nConfigService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageIndex=1&orderBy=abc:DESC
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @GetMapping
    public JsonResult<Collection<List<I18nConfigVO>>> getViewObjectListMapping(I18nConfig entity, Pagination pagination) throws Exception {
        return JsonResult.OK(i18nConfigService.getI18nList(entity, pagination)).bindPagination(pagination);
    }

    /**
     * 获取指定消息代码的配置列表
     *
     * @param code
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @GetMapping("/{code}")
    public JsonResult<List<I18nConfig>> getViewObjectMapping(@PathVariable String code) throws Exception {
        return JsonResult.OK(i18nConfigService.getEntityList(Wrappers.<I18nConfig>lambdaQuery().eq(I18nConfig::getCode ,code)));
    }

    /**
     * 创建或更新资源对象
     *
     * @param entityList
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_BATCH_UPDATE)
    @BindPermission(name = OperationCons.LABEL_BATCH_UPDATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult<?> createEntityMapping(@Valid @RequestBody ValidList<I18nConfig> entityList) throws Exception {
        return new JsonResult<>(i18nConfigService.createOrUpdateEntities(entityList));
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @PostMapping("/batch-delete")
    public JsonResult<?> deleteEntityMapping(@RequestBody List<String> ids) throws Exception {
        return new JsonResult<>(i18nConfigService.deleteEntities(ids));
    }

    /**
     * 检查是否有重复的code
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/check-code-duplicate/{code}")
    public JsonResult checkTempCodeDuplicate(@PathVariable String code, @RequestBody List<String> ids) throws Exception {
        if (V.isEmpty(code)) {
            return JsonResult.OK();
        }
        LambdaQueryWrapper<I18nConfig> wrapper = Wrappers.<I18nConfig>lambdaQuery().eq(I18nConfig::getCode, code);
        wrapper.notIn(V.notEmpty(ids), I18nConfig::getId, ids);
        if (i18nConfigService.exists(wrapper)) {
            return JsonResult.FAIL_VALIDATION("资源标识[" + code + "]已存在");
        }
        return JsonResult.OK();
    }

}
