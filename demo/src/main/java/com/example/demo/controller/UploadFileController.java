package com.example.demo.controller;  

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.diboot.file.controller.BaseFileController;
import com.diboot.file.dto.UploadFileDTO;
import com.diboot.file.dto.UploadFileFormDTO;
import com.diboot.file.entity.UploadFile;
import com.diboot.core.entity.Dictionary;
import com.diboot.core.vo.*;
import com.diboot.core.util.S;

import com.diboot.file.util.HttpHelper;
import com.diboot.file.util.ImageHelper;
import com.diboot.file.vo.UploadFileVO;
import com.diboot.iam.annotation.BindPermission;
import com.diboot.iam.annotation.Log;
import com.diboot.iam.annotation.OperationCons;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
* 文件上传相关Controller
*
* @author MyName
* @version 1.0
* @date 2022-12-30
* * Copyright © MyCompany
*/

@RestController
@RequestMapping("/uploadFile")
@BindPermission(name = "上传文件", code = "UploadFile")
@Slf4j
public class UploadFileController extends BaseFileController {

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
    public JsonResult getViewObjectListMapping(UploadFileDTO queryDto, Pagination pagination) throws Exception {
        QueryWrapper<UploadFile> queryWrapper = super.buildQueryWrapperByDTO(queryDto);
        // 查询当前页的数据
        List<UploadFileVO> voList = uploadFileService.getViewObjectList(queryWrapper, pagination, UploadFileVO.class);
        // 返回结果
        return JsonResult.OK(voList).bindPagination(pagination);
    }


    /**
     * 根据资源id查询ViewObject
     *
     * @param uuid ID
     * @return
     * @throws Exception
     */
    @Log(operation = OperationCons.LABEL_DETAIL)
    @BindPermission(name = OperationCons.LABEL_DETAIL, code = OperationCons.CODE_READ)
    @GetMapping("/{id}")
    public JsonResult getViewObjectMapping(@PathVariable("id") String uuid) throws Exception {
        return JsonResult.OK(uploadFileService.getViewObject(uuid, UploadFileVO.class));
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
    public JsonResult updateEntityMapping(@RequestBody UploadFile entity) throws Exception {
        return new JsonResult(uploadFileService.updateEntity(entity));
    }

    /**
     * 上传文件
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public JsonResult uploadMapping(@RequestParam("file") MultipartFile file) throws Exception{
        return super.uploadFile(file, Dictionary.class);
    }

    /**
     * 上传文件 by dto
     *
     * @param uploadFileFormDTO
     * @return
     * @throws Exception
     */
    @PostMapping("/upload/dto")
    public JsonResult uploadMapping(UploadFileFormDTO uploadFileFormDTO) throws Exception{
        return super.uploadFile(uploadFileFormDTO);
    }
    
    /**
     * 下载文件
     **/
    @GetMapping("/download/{fileUuid}")
    public JsonResult download(@PathVariable("fileUuid")String fileUuid, HttpServletResponse response) throws Exception {
        if(S.contains(fileUuid, ".")){
            fileUuid = S.substringBefore(fileUuid, ".");
        }
        UploadFile uploadFile = uploadFileService.getEntity(fileUuid);
        if(uploadFile == null){
            return new JsonResult(Status.FAIL_VALIDATION, "文件不存在");
        }
        // 下载
        HttpHelper.downloadLocalFile(uploadFile.getStoragePath(), uploadFile.getFileName(), response);
        return null;
    }
    
    /**
     * 显示图片
     *
     * @param fileUuid
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/download/{fileUuid}/image")
    public JsonResult writeImage(@PathVariable("fileUuid") String fileUuid, HttpServletResponse response) throws Exception {
        if (S.contains(fileUuid, ".")) {
            fileUuid = S.substringBefore(fileUuid, ".");
        }
        UploadFile uploadFile = uploadFileService.getEntity(fileUuid);
        if (uploadFile == null) {
            return new JsonResult(Status.FAIL_VALIDATION, "图片不存在");
        }
        if (!ImageHelper.isImage(uploadFile.getFileType())) {
            log.warn("非图片文件:{}", fileUuid);
            return JsonResult.FAIL_VALIDATION("非图片文件");
        }
        IOUtils.copy(FileUtils.openInputStream(new File(uploadFile.getStoragePath())), response.getOutputStream());
        return null;
    }
    
    /**
     * <h3>获取文件通用接口</h3>
     * <p>
     * 其中当relObjField不传递的时候，表示获取当前业务ID和业务类型下的所有文件<br/>
     * 当传递relObjField的时候，获取指定类型的文件
     * </p>
     *
     * @param relObjId   业务ID   <strong style="color:red;">必传字段</strong>
     * @param relObjType 业务类型 <strong style="color:red;">必传字段</strong>
     * @param relObjField 对应的具体类型   <strong style="color:blue;">非必传字段(同一种业务下可能有多种文件)</strong>
     * @return {@link List <UploadFile>} 返回文件对象的集合
     * @throws Exception
     */
    @GetMapping(value = {"/getList/{relObjId}/{relObjType}", "/getList/{relObjId}/{relObjType}/{relObjField}"})
    public JsonResult getFileList(@PathVariable("relObjId") Object relObjId, @PathVariable("relObjType") String relObjType,
                              @PathVariable(value = "relObjField", required = false) String relObjField) throws Exception {
        List<UploadFile> uploadFiles = getUploadFileList(relObjId, relObjType, relObjField);
        return JsonResult.OK(uploadFiles);
    }
}