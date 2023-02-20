package com.example.demo.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.dto.SortParamDTO;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.cache.IamCacheManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamResourceDTO;
import com.diboot.iam.entity.IamResource;
import com.diboot.iam.service.IamResourceService;
import com.diboot.iam.vo.IamResourceListVO;
import com.diboot.iam.vo.IamResourceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统资源权限相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/resource")
@Slf4j
@BindPermission(name = "系统资源权限")
public class ResourceController extends BaseCrudRestController<IamResource> {

    @Autowired
    private IamResourceService iamResourceService;

    /***
     * 查询ViewObject的分页数据
     * <p>
     * url请求参数示例: ?field=abc&pageSize=20&pageIndex=1&orderBy=id
     * </p>
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_LIST)
    @BindPermission(name = OperationCons.LABEL_LIST, code = OperationCons.CODE_READ)
    @GetMapping
    public JsonResult getViewObjectListMapping(IamResource entity) throws Exception {
        QueryWrapper<IamResource> queryWrapper = super.buildQueryWrapperByQueryParams(entity);
        queryWrapper.lambda().orderByAsc(IamResource::getSortId);
        List<IamResourceListVO> voList = iamResourceService.getViewObjectList(queryWrapper, null, IamResourceListVO.class);
        Map<String, Object> paramsMap = getParamsMap();
        if (!paramsMap.containsKey("displayName") && !paramsMap.containsKey("resourceCode")) {
            voList = BeanUtils.buildTree(voList, Cons.TREE_ROOT_ID);
        }
        return JsonResult.OK(voList);
    }

    /**
     * 获取菜单树
     *
     * @return
     */
    @GetMapping("/menu-tree")
    public JsonResult getMenuTreeList() {
        LambdaQueryWrapper<IamResource> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.ne(IamResource::getDisplayType, "PERMISSION");
        queryWrapper.orderByAsc(IamResource::getSortId).orderByAsc(IamResource::getId);
        List<IamResourceListVO> list = iamResourceService.getViewObjectList(queryWrapper, null, IamResourceListVO.class);
        return JsonResult.OK(BeanUtils.buildTree(list, Cons.TREE_ROOT_ID));
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
    public JsonResult getViewObjectMapping(@PathVariable("id") String id) throws Exception {
        return super.getViewObject(id, IamResourceVO.class);
    }

    /***
     * 新建菜单项、按钮/权限列表
     * @param IamResourceDTO
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping
    public JsonResult createEntityMapping(@Valid @RequestBody IamResourceDTO IamResourceDTO) throws Exception {
        iamResourceService.createMenuResources(IamResourceDTO);
        return JsonResult.OK(IamResourceDTO.getId());
    }

    /***
     * 更新用户、账号和用户角色关联列表
     * @param IamResourceDTO
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") String id, @Valid @RequestBody IamResourceDTO IamResourceDTO) throws Exception {
        iamResourceService.updateMenuResources(IamResourceDTO);
        return JsonResult.OK();
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id") String id) throws Exception {
        iamResourceService.deleteMenuResources(id);
        return JsonResult.OK();
    }

    /**
     * 排序
     *
     * @param sortParam
     * @return
     */
    @PatchMapping("/sort")
    @BindPermission(name = "列表排序", code = OperationCons.CODE_WRITE)
    public JsonResult<?> sort(@RequestBody @Valid SortParamDTO<String> sortParam) {
        return new JsonResult<>(getService().sort(sortParam, IamResource::getSortId, IamResource::getParentId, null));
    }

    /***
     * api接口列表（供前端选择）
     * @return
     * @throws Exception
     */
    @GetMapping("/api-list")
    public JsonResult apiList() throws Exception {
        return JsonResult.OK(IamCacheManager.getApiPermissionVoList());
    }

    /***
     * 检查菜单编码是否重复
     * @param id
     * @param code
     * @return
     */
    @GetMapping("/check-code-duplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) String id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamResource> wrapper = Wrappers.<IamResource>lambdaQuery()
                    .select(IamResource::getId)
                    .eq(IamResource::getResourceCode, code)
                    .ne(IamResource::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.PERMISSION.name())
                    .ne(V.notEmpty(id),IamResource::getId, id);
            boolean exists = iamResourceService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("编码已存在: " + code);
            }
        }
        return JsonResult.OK();
    }

}