package com.example.demo.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.dto.IamPositionFormDTO;
import com.diboot.iam.dto.IamUserPositionBatchDTO;
import com.diboot.iam.entity.IamPosition;
import com.diboot.iam.entity.IamUserPosition;
import com.diboot.iam.service.IamPositionService;
import com.diboot.iam.vo.IamPositionVO;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import java.util.List;

/**
 * 岗位 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/position")
@BindPermission(name = "岗位")
@Slf4j
public class IamPositionController extends BaseCrudRestController<IamPosition> {
    @Autowired
    private IamPositionService iamPositionService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: /list?field=abc&pageIndex=1&orderBy=abc:DESC
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamPosition entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamPositionVO.class);
    }

    /**
     * 根据资源id查询ViewObject
     *
     * @param id ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectWithMapping(@PathVariable("id") Long id) throws Exception{
        return super.getViewObject(id, IamPositionVO.class);
    }
    
    /**
     * 根据用户信息获取岗位列表
     *
     * @param userType
     * @param userId
     * @return
     */
    @Log(operation = "根据用户信息获取岗位列表")
    @BindPermission(name = "根据用户信息获取岗位列表", code = OperationCons.CODE_READ)
    @GetMapping("/listUserPositions/{userType}/{userId}")
    public JsonResult listUserPositionsByUser(@PathVariable("userType") String userType, @PathVariable("userId") Long userId) {
        List<IamUserPosition> userPositionList = iamPositionService.getUserPositionListByUser(userType, userId);
        return JsonResult.OK(userPositionList);
    }

    /**
     * 创建资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping("/")
    public JsonResult createEntityWithMapping(@RequestBody @Valid IamPositionFormDTO entity) throws Exception {
        return super.createEntity(entity);
    }

    /**
     * 根据ID更新资源对象
     *
     * @param entity
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityWithMapping(@PathVariable("id")Long id, @Valid @RequestBody IamPositionFormDTO entity) throws Exception {
        return super.updateEntity(id, entity);
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntity(@PathVariable("id")Long id) throws Exception {
        return super.deleteEntity(id);
    }

    /**
     * 检查编码是否重复
     *
     * @param id
     * @param code
     * @return
     */
    @GetMapping("/checkCodeDuplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) Long id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamPosition> wrapper = Wrappers.<IamPosition>lambdaQuery()
                    .select(IamPosition::getId).eq(IamPosition::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamPosition::getId, id);
            }
            boolean exists = iamPositionService.exists(wrapper);
            if (exists) {
                return new JsonResult(Status.FAIL_OPERATION, "编码已存在: "+code);
            }
        }
        return JsonResult.OK();
    }

    /**
     * 批量更新关联关系
     *
     * @param userPositionBatchDTO
     * @return
     * @throws Exception
     */
    @Log(operation = "设置用户岗位关系")
    @BindPermission(name = "设置用户岗位关系", code = OperationCons.CODE_WRITE)
    @PostMapping("/batchUpdateUserPositionRelations")
    public JsonResult batchUpdate(@RequestBody IamUserPositionBatchDTO userPositionBatchDTO) throws Exception {
        iamPositionService.updateUserPositionRelations(userPositionBatchDTO.getUserType(), userPositionBatchDTO.getUserId(), userPositionBatchDTO.getUserPositionList());
        return JsonResult.OK();
    }

} 