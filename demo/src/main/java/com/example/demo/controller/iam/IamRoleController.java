package com.example.demo.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.exception.BusinessException;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamRoleFormDTO;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.entity.IamRole;
import com.diboot.iam.service.IamResourcePermissionService;
import com.diboot.iam.service.IamRoleResourceService;
import com.diboot.iam.service.IamRoleService;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import com.diboot.iam.vo.IamRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 角色相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/role")
@Slf4j
@BindPermission(name = "角色")
public class IamRoleController extends BaseCrudRestController<IamRole> {

    @Autowired
    private IamRoleService iamRoleService;

    @Autowired
    private IamResourcePermissionService iamResourcePermissionService;

    @Autowired
    private IamRoleResourceService iamRoleResourceService;

    /**
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: /list?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     *
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping("/list")
    public JsonResult getViewObjectListMapping(IamRole entity, Pagination pagination) throws Exception{
        return super.getViewObjectList(entity, pagination, IamRoleVO.class);
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
    public JsonResult getViewObjectMapping(@PathVariable("id")Long id) throws Exception{
        IamRoleVO roleVO = iamRoleService.getViewObject(id, IamRoleVO.class);
        if (V.notEmpty(roleVO.getPermissionList())){
            List<Long> permissionIdList = BeanUtils.collectIdToList(roleVO.getPermissionList());
            List<IamResourcePermissionListVO> permissionVOList = iamResourcePermissionService.getViewObjectList(
                Wrappers.<IamResourcePermission>lambdaQuery().in(IamResourcePermission::getId, permissionIdList),
                null,
                IamResourcePermissionListVO.class
            );
            permissionVOList = BeanUtils.buildTree(permissionVOList);
            roleVO.setPermissionVOList(permissionVOList);
        }
        return JsonResult.OK(roleVO);
    }

    /**
     * 新建角色和角色权限关联列表
     *
     * @param roleFormDTO
     * @return
     * @throws Exception
     */
		@Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamRoleFormDTO roleFormDTO) throws Exception {
        return super.createEntity(roleFormDTO);
    }

    /**
     * 更新角色和角色权限关联列表
     *
     * @param roleFormDTO
     * @return JsonResult
     * @throws Exception
     */
		@Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody IamRoleFormDTO roleFormDTO) throws Exception {
        return super.updateEntity(id, roleFormDTO);
    }

    /**
     * 根据id删除资源对象
     *
     * @param id
     * @return
     * @throws Exception
     */
		@Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id")Long id) throws Exception {
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
            LambdaQueryWrapper<IamRole> wrapper = Wrappers.<IamRole>lambdaQuery()
                .select(IamRole::getId).eq(IamRole::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamRole::getId, id);
            }
            boolean exists = iamRoleService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("编码已存在: "+code);
            }
        }
        return JsonResult.OK();
    }

    @Override
    protected String beforeUpdate(IamRole entity) throws Exception {
        if (Cons.ROLE_SUPER_ADMIN.equals(entity.getCode())){
            throw new BusinessException(Status.FAIL_OPERATION, "不能更新超级管理员角色");
        }
        return null;
    }

    @Override
    protected String beforeDelete(IamRole entity) throws Exception {
        if (Cons.ROLE_SUPER_ADMIN.equals(entity.getCode())){
            throw new BusinessException(Status.FAIL_OPERATION, "不能删除超级管理员角色");
        }
        return null;
    }

    @Override
    protected void afterCreated(IamRole entity) throws Exception {
        IamRoleFormDTO roleFormDTO = (IamRoleFormDTO) entity;
        iamRoleResourceService.createRoleResourceRelations(roleFormDTO.getId(), roleFormDTO.getPermissionIdList());
    }

    @Override
    protected void afterUpdated(IamRole entity) throws Exception {
        IamRoleFormDTO roleFormDTO = (IamRoleFormDTO) entity;
        iamRoleResourceService.updateRoleResourceRelations(roleFormDTO.getId(), roleFormDTO.getPermissionIdList());
    }
}