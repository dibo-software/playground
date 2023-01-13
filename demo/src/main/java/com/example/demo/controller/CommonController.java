package com.example.demo.controller;

import com.diboot.core.controller.BaseController;
import com.diboot.core.dto.AttachMoreDTO;
import com.diboot.core.entity.ValidList;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.LabelValue;
import com.diboot.iam.config.Cons;
import com.diboot.iam.util.IamSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 通用接口相关 Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {

    /**
     * 获取附加属性的通用Options接口，用于初始化前端下拉框选项。
     * <p>
     * 如数据量过大，请调用通用Options过滤接口：{@link #attachMoreFilter(AttachMoreDTO, String, String)}
     *
     * @param attachMoreDTOList 关联数据列表
     * @return Options集合
     */
    @PostMapping("/attachMore")
    public JsonResult<Map<String, List<LabelValue>>> attachMore(@Valid @RequestBody ValidList<AttachMoreDTO> attachMoreDTOList) {
        return JsonResult.OK(super.attachMoreRelatedData(attachMoreDTOList));
    }

    /**
     * 获取附加属性的通用Options过滤接口，用于前端下拉框选择远程搜索 或 异步加载（树|级联）数据。
     * <p>
     * 适用于数据量大时远程过滤获取选项数据，或分层级获取数据
     *
     * @param attachMoreDTO
     * @param parentType
     * @param parentValue
     * @return
     */
    @PostMapping({"/attachMoreFilter", "/attachMoreFilter/{parentValue}", "/attachMoreFilter/{parentType}/{parentValue}"})
    public JsonResult<List<LabelValue>> attachMoreFilter(@Valid @RequestBody AttachMoreDTO attachMoreDTO,
                                                         @PathVariable(value = "parentValue", required = false) String parentValue,
                                                         @PathVariable(value = "parentType", required = false) String parentType) {
        return JsonResult.OK(super.attachMoreRelatedData(attachMoreDTO, parentValue, parentType));
    }
    /**
     * attachMore对象 自定义权限检查点
     *
     * @param attachMore
     * @return 返回true放行
     */
    @Override
    protected boolean attachMoreSecurityCheck(AttachMoreDTO attachMore) {
        // 超级管理员
        if (IamSecurityUtils.getSubject().hasRole(Cons.ROLE_SUPER_ADMIN)) {
            return true;
        }
        try {
            IamSecurityUtils.getSubject().checkPermission(attachMore.getTarget() + ":read");
        } catch (Exception e) {
            log.warn("无权获取attachMore: {}", attachMore.getTarget());
            return false;
        }
        return true;
    }

}
