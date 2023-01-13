package com.example.demo.controller.iam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.BeanUtils;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.cache.IamCacheManager;
import com.diboot.iam.config.Cons;
import com.diboot.iam.dto.IamResourcePermissionDTO;
import com.diboot.iam.entity.IamResourcePermission;
import com.diboot.iam.service.IamResourcePermissionService;
import com.diboot.iam.vo.IamResourcePermissionListVO;
import com.diboot.iam.vo.IamResourcePermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
* 系统资源权限相关Controller
* @author MyName
* @version 1.0
* @date 2022-12-30
* Copyright © MyCompany
*/
@RestController
@RequestMapping("/iam/resourcePermission")
@Slf4j
@BindPermission(name = "系统资源权限")
public class IamResourcePermissionController extends BaseCrudRestController<IamResourcePermission> {

    @Autowired
    private IamResourcePermissionService iamResourcePermissionService;

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
    public JsonResult getViewObjectListMapping(IamResourcePermission entity) throws Exception{
        QueryWrapper<IamResourcePermission> queryWrapper = super.buildQueryWrapperByQueryParams(entity);
        queryWrapper.lambda().orderByDesc(IamResourcePermission::getSortId, IamResourcePermission::getId);
        List<IamResourcePermissionListVO> voList = iamResourcePermissionService.getViewObjectList(queryWrapper, null, IamResourcePermissionListVO.class);
        Map<String, Object> paramsMap = getParamsMap();
        if (!paramsMap.containsKey("displayName") && !paramsMap.containsKey("resourceCode")) {
            voList = BeanUtils.buildTree(voList);
        }
        return JsonResult.OK(voList);
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
    public JsonResult getViewObjectMapping(@PathVariable("id")Long id) throws Exception{
        return super.getViewObject(id, IamResourcePermissionVO.class);
    }

    /***
    * 新建菜单项、按钮/权限列表
    * @param iamResourcePermissionDTO
    * @return
    * @throws Exception
    */
    @Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamResourcePermissionDTO iamResourcePermissionDTO) throws Exception {
        iamResourcePermissionService.createMenuAndPermissions(iamResourcePermissionDTO);
        return JsonResult.OK();
    }

    /***
    * 更新用户、账号和用户角色关联列表
    * @param iamResourcePermissionDTO
    * @return JsonResult
    * @throws Exception
    */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody IamResourcePermissionDTO iamResourcePermissionDTO) throws Exception {
        iamResourcePermissionService.updateMenuAndPermissions(iamResourcePermissionDTO);
        return JsonResult.OK();
    }

    /***
    * 删除用户、账号和用户角色关联列表
    * @param id
    * @return
    * @throws Exception
    */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id")Long id) throws Exception {
        iamResourcePermissionService.deleteMenuAndPermissions(id);
        return JsonResult.OK();
    }

    /***
    * 加载更多数据
    * @return
    * @throws Exception
    */
    @GetMapping("/attachMore")
    public JsonResult attachMore(ModelMap modelMap) throws Exception{
        // 获取关联表数据IamResourcePermission的树状列表
        List<IamResourcePermissionListVO> menuList = iamResourcePermissionService.getViewObjectList(
            Wrappers.<IamResourcePermission>lambdaQuery().eq(IamResourcePermission::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name()),
            null,
            IamResourcePermissionListVO.class
        );
        modelMap.put("menuTree", BeanUtils.buildTree(menuList));
        return JsonResult.OK(modelMap);
    }

    /**
    * 列表排序
    * @param permissionList
    * @return
    * @throws Exception
    */
    @PostMapping("/sortList")
    @BindPermission(name="列表排序", code = OperationCons.CODE_WRITE)
    public JsonResult sortList(@RequestBody List<IamResourcePermission> permissionList) throws Exception {
        iamResourcePermissionService.sortList(permissionList);
        return JsonResult.OK().msg("更新成功");
    }

    /***
    * api接口列表（供前端选择）
    * @return
    * @throws Exception
    */
    @GetMapping("/apiList")
    public JsonResult apiList() throws Exception{
        return JsonResult.OK(IamCacheManager.getApiPermissionVoList());
    }

    /***
    * 检查菜单编码是否重复
    * @param id
    * @param code
    * @return
    */
    @GetMapping("/checkCodeDuplicate")
    public JsonResult checkCodeDuplicate(@RequestParam(required = false) Long id, @RequestParam String code) {
        if (V.notEmpty(code)) {
            LambdaQueryWrapper<IamResourcePermission> wrapper = Wrappers.<IamResourcePermission>lambdaQuery()
                .select(IamResourcePermission::getId)
                .eq(IamResourcePermission::getResourceCode, code)
                .eq(IamResourcePermission::getDisplayType, Cons.RESOURCE_PERMISSION_DISPLAY_TYPE.MENU.name());
            if (V.notEmpty(id)) {
                wrapper.ne(IamResourcePermission::getId, id);
            }
            boolean exists = iamResourcePermissionService.exists(wrapper);
            if (exists) {
                return JsonResult.FAIL_VALIDATION("编码已存在: "+code);
            }
        }
        return JsonResult.OK();
    }

}