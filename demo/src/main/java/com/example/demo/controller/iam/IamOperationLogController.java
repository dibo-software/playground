package com.example.demo.controller.iam;  

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.entity.IamOperationLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
* 操作日志相关Controller
* @author MyName
* @version 1.0
* @date 2022-12-30
* Copyright © MyCompany
*/
@Slf4j
@BindPermission(name = "操作日志")
@RestController
@RequestMapping("/iam/operationLog")
public class IamOperationLogController extends BaseCrudRestController<IamOperationLog> {

    /***
    * 查询ViewObject的分页数据
    * <p>
    * url请求参数示例: /list?field=abc&pageSize=20&pageIndex=1&orderBy=id
    * </p>
    * @return
    * @throws Exception
    */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamOperationLog entity, Pagination pagination) throws Exception{
        QueryWrapper<IamOperationLog> queryWrapper = super.buildQueryWrapperByQueryParams(entity);
        Integer status = getInteger("status");
        if(status != null){
            if(status.intValue() == 0){
                queryWrapper.eq("status_code", 0);
            }
            else{
                queryWrapper.gt("status_code", 0);
            }
        }
        return super.getEntityListWithPaging(queryWrapper, pagination);
    }

    /***
    * 根据资源id查询ViewObject
    * @param id ID
    * @return
    * @throws Exception
    */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") Long id) throws Exception{
        IamOperationLog operationLog = super.getEntity(id);
        return JsonResult.OK(operationLog);
    }
}
