package com.example.demo.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.core.vo.Status;
import com.diboot.iam.dto.IamOrgDTO;
import com.diboot.iam.entity.IamOrg;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamOrgService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.vo.IamOrgVO;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.config.Cons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 组织机构 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/org")
@BindPermission(name = "组织机构")
@Slf4j
public class IamOrgController extends BaseCrudRestController<IamOrg> {
    @Autowired
    private IamOrgService iamOrgService;
    @Autowired
    private IamUserService iamUserService;
    
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
    public JsonResult getViewObjectListWithMapping(IamOrg entity, Pagination pagination) throws Exception {
        QueryWrapper<IamOrg> queryWrapper = super.buildQueryWrapperByQueryParams(entity);
        queryWrapper.lambda().orderByDesc(IamOrg::getSortId).orderByDesc(IamOrg::getId);
        pagination.clearDefaultOrder();
        List<IamOrgVO> voList = this.getService().getViewObjectList(queryWrapper, pagination, IamOrgVO.class);
        return JsonResult.OK(voList).bindPagination(pagination);
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
    public JsonResult getViewObjectMapping(@PathVariable("id") Long id) throws Exception {
        return super.getViewObject(id, IamOrgVO.class);
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
    public JsonResult createEntityMapping(@Valid @RequestBody IamOrg entity) throws Exception {
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
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody IamOrg entity) throws Exception {
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
    public JsonResult deleteEntityWithMapping(@PathVariable("id") Long id) throws Exception {
        boolean existChildren = iamOrgService.exists(IamOrg::getParentId, id);
        if (existChildren) {
            return JsonResult.FAIL_OPERATION("该部门存在子部门，不允许删除");
        }
        boolean hasUser = iamUserService.exists(IamUser::getOrgId, id);
        if (hasUser) {
            return JsonResult.FAIL_OPERATION("该部门下存在有效用户，不允许删除");
        }
        return deleteEntity(id);
    }

    /**
     * 获取根节点的组织树
     */
    @BindPermission(name = "获取组织树", code = OperationCons.CODE_READ)
    @GetMapping("/tree")
    public JsonResult getRootNodeOrgTree() throws Exception {
        List<IamOrgVO> orgVOList = iamOrgService.getOrgTree(IamOrg.VIRTUAL_ROOT_ID);
        return JsonResult.OK(orgVOList);
    }
    
    /**
     * 获取指定节点的子节点
     *
     * @param parentNodeId
     * @return
     * @throws Exception
     */
    @BindPermission(name = "查看子组织树", code = OperationCons.CODE_READ)
    @GetMapping("/tree/{parentNodeId}")
    public JsonResult getOrgChildNodes(@PathVariable("parentNodeId") Long parentNodeId) throws Exception {
        List<IamOrgVO> orgVOList = iamOrgService.getOrgTree(parentNodeId);
        return JsonResult.OK(orgVOList);
    }

    /**
     * 获取指定节点的子列表
     *
     * @return
     * @throws Exception
     */
    @BindPermission(name = "获取子组织列表", code = OperationCons.CODE_READ)
    @GetMapping("/childrenList/{parentNodeId}")
    public JsonResult getOrgChildList(@PathVariable("parentNodeId") Long parentNodeId, IamOrgDTO iamOrgDTO, Pagination pagination) throws Exception {
        QueryWrapper<IamOrg> wrapper = super.buildQueryWrapperByQueryParams(iamOrgDTO);
        if (parentNodeId != null && !V.equals(parentNodeId, 0L)) {
            wrapper.lambda().eq(IamOrg::getParentId, parentNodeId);
        }
        return super.getEntityListWithPaging(wrapper, pagination);
    }

		/**
     * 列表排序
     *
     * @param orgList
     * @return
     * @throws Exception
     */
    @PostMapping("/sortList")
    @Log(operation = "sortList")
    @BindPermission(name = "列表排序", code = OperationCons.CODE_WRITE)
    public JsonResult sortList(@RequestBody List<IamOrg> orgList) throws Exception {
        iamOrgService.sortList(orgList);
        return JsonResult.OK().msg("更新成功");
    }
    
    /**
     * 检查code是否重复
     *
     * @param id
     * @param code
     * @return
     */
    @GetMapping("/checkCodeDuplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) Long id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamOrg> wrapper = Wrappers.<IamOrg>lambdaQuery().select(IamOrg::getId).eq(IamOrg::getCode, code);
            if (V.notEmpty(id)) {
                wrapper.ne(IamOrg::getId, id);
            }
            boolean exists = iamOrgService.exists(wrapper);
            if (exists) {
                return new JsonResult(Status.FAIL_OPERATION, "编码已存在: " + code);
            }
        }
        return JsonResult.OK();
    }
} 