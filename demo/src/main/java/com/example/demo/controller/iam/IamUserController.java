package com.example.demo.controller.iam;  

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper; 
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.util.V; 
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Pagination;
import com.diboot.iam.annotation.BindPermission; 
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import com.diboot.iam.dto.ChangePwdDTO;
import com.diboot.iam.dto.IamUserAccountDTO;
import com.diboot.iam.dto.BaseUserInfoDTO;
import com.diboot.iam.dto.IamUserSearchDTO;
import com.diboot.iam.entity.IamAccount;
import com.diboot.iam.entity.IamUser;
import com.diboot.iam.service.IamAccountService;
import com.diboot.iam.service.IamUserService;
import com.diboot.iam.util.IamSecurityUtils;
import com.diboot.iam.vo.IamUserVO;
import com.diboot.iam.vo.IamUserOrgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统用户相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@RestController
@RequestMapping("/iam/user")
@Slf4j
@BindPermission(name = "用户")
public class IamUserController extends BaseCrudRestController<IamUser> {
    
    @Autowired
    private IamUserService iamUserService;

    @Autowired
    private IamAccountService iamAccountService;

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
    public JsonResult getViewObjectListMapping(IamUserSearchDTO dto, Pagination pagination) throws Exception{
        Long orgId = dto.getOrgId();
        dto.setOrgId(null);

        QueryWrapper<IamUser> queryWrapper = super.buildQueryWrapperByQueryParams(dto);

        List<IamUserVO> voList = iamUserService.getUserViewList(queryWrapper, pagination, orgId);
        // 返回结果
        return  JsonResult.OK(voList).bindPagination(pagination);
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
        return super.getViewObject(id, IamUserOrgVO.class);
    }

    /**
     * 新建用户、账号和用户角色关联列表
     *
     * @param iamUserAccountDTO
     * @return
     * @throws Exception
     */
		@Log(operation = OperationCons.LABEL_CREATE)
    @BindPermission(name = OperationCons.LABEL_CREATE, code = OperationCons.CODE_WRITE)
    @PostMapping("/")
    public JsonResult createEntityMapping(@Valid @RequestBody IamUserAccountDTO iamUserAccountDTO) throws Exception {
        iamUserService.createUserAndAccount(iamUserAccountDTO);
        return JsonResult.OK();
    }

    /**
     * 更新用户、账号和用户角色关联列表
     *
     * @param iamUserAccountDTO
     * @return JsonResult
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_UPDATE)
    @BindPermission(name = OperationCons.LABEL_UPDATE, code = OperationCons.CODE_WRITE)
    @PutMapping("/{id}")
    public JsonResult updateEntityMapping(@PathVariable("id") Long id, @Valid @RequestBody IamUserAccountDTO iamUserAccountDTO) throws Exception {
        iamUserService.updateUserAndAccount(iamUserAccountDTO);
        return JsonResult.OK();
    }

    /**
     * 删除用户、账号和用户角色关联列表
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DELETE)
    @BindPermission(name = OperationCons.LABEL_DELETE, code = OperationCons.CODE_WRITE)
    @DeleteMapping("/{id}")
    public JsonResult deleteEntityMapping(@PathVariable("id")Long id) throws Exception {
        iamUserService.deleteUserAndAccount(id);
        return JsonResult.OK();
    }

    /**
     * 获取用户名
     *
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/getUsername/{id}")
    public JsonResult getUsername(@PathVariable("id")Long id) throws Exception{
        IamAccount account = iamAccountService.getSingleEntity(
            Wrappers.<IamAccount>lambdaQuery()
            .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
            .eq(IamAccount::getUserId, id)
        );
        return JsonResult.OK(account != null ? account.getAuthAccount() : null).msg("获取用户名成功");
    }

    /**
     * 获取指定orgId下的用户列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/getUserList/{orgId}")
    public JsonResult getUserList(@PathVariable("orgId") Long orgId, IamUser iamUser, Pagination pagination) throws Exception {
        QueryWrapper<IamUser> wrapper = super.buildQueryWrapperByQueryParams(iamUser);
        if (orgId != null && !V.equals(orgId, 0L)) {
            wrapper.lambda().eq(IamUser::getOrgId, orgId);
        }
        List entityList = this.getService().getEntityList(wrapper, pagination);
        return JsonResult.OK(entityList).bindPagination(pagination);
    }

    /**
     * 获取当前用户信息
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/getCurrentUserInfo")
    public JsonResult getCurrentUserInfo() throws Exception{
        IamUser iamUser = IamSecurityUtils.getCurrentUser();
        IamUser newIamUser = iamUserService.getEntity(iamUser.getId());
        return JsonResult.OK(newIamUser);
    }

    /**
     * 更新当前用户信息
     *
     * @param baseUserInfoDTO
     * @return
     * @throws Exception
     */
		@Log(operation = "更新个人信息")
    @PostMapping("/updateCurrentUserInfo")
    public JsonResult updateCurrentUserInfo(@Valid @RequestBody BaseUserInfoDTO baseUserInfoDTO) throws Exception{
        IamUser iamUser = IamSecurityUtils.getCurrentUser();
        IamUser newIamUser = iamUserService.getEntity(iamUser.getId());
        newIamUser.setRealname(baseUserInfoDTO.getRealname())
                .setGender(baseUserInfoDTO.getGender())
                .setMobilePhone(baseUserInfoDTO.getMobilePhone())
                .setEmail(baseUserInfoDTO.getEmail())
                .setAvatarUrl(baseUserInfoDTO.getAvatarUrl());
        boolean success = iamUserService.updateEntity(newIamUser);
        if (!success){
            return JsonResult.FAIL_OPERATION("更新个人信息失败");
        }
        return JsonResult.OK(newIamUser).msg("更新成功");
    }

    /**
     * 更改密码
     *
     * @param changePwdDTO
     * @return
     * @throws Exception
     */
		@Log(operation = "更改密码")
    @PostMapping("/changePwd")
    public JsonResult changePwd(@Valid @RequestBody ChangePwdDTO changePwdDTO) throws Exception{
        IamUser iamUser = IamSecurityUtils.getCurrentUser();
        IamAccount iamAccount = iamAccountService.getSingleEntity(
            Wrappers.<IamAccount>lambdaQuery()
                    .eq(IamAccount::getUserType, IamUser.class.getSimpleName())
                    .eq(IamAccount::getUserId, iamUser.getId())
        );
        boolean success = iamAccountService.changePwd(changePwdDTO, iamAccount);
        if (!success){
            return JsonResult.FAIL_OPERATION("更改密码失败");
        }
        return JsonResult.OK().msg("更改密码成功");
    }

    /**
     * 校验用户名是否重复
     *
     * @param id
     * @param username
     * @return
     */
    @GetMapping("/checkUsernameDuplicate")
    public JsonResult checkUsernameDuplicate(@RequestParam(required = false) Long id, @RequestParam String username) {
        if (V.isEmpty(username)) {
            return JsonResult.FAIL_OPERATION("用户名不能为空");
        }
        IamAccount account = new IamAccount().setAuthAccount(username);
        account.setId(id);
        boolean alreadyExists = iamAccountService.isAccountExists(account);
        if (alreadyExists) {
            return JsonResult.FAIL_OPERATION("用户名已存在");
        }
        return JsonResult.OK();
    }

    /**
     * 校验用户编号是否重复
     *
     * @param id
     * @param userNum
     * @return
     */
    @GetMapping("/checkUserNumDuplicate")
    public JsonResult checkUserNumDuplicate(@RequestParam(required = false) Long id, @RequestParam String userNum) {
        if (V.isEmpty(userNum)) {
            return JsonResult.FAIL_OPERATION("用户编号不能为空");
        }
        boolean alreadyExists = iamUserService.isUserNumExists(id, userNum);
        if (alreadyExists) {
            return JsonResult.FAIL_OPERATION("用户编号已存在");
        }
        return JsonResult.OK();
    }
}
