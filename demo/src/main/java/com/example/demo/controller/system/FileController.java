package com.example.demo.controller.system;

import com.diboot.core.util.S;
import com.diboot.core.util.V;
import com.diboot.core.vo.JsonResult;
import com.diboot.core.vo.Status;
import com.diboot.file.entity.FileRecord;
import com.diboot.file.service.FileRecordService;
import com.diboot.file.service.FileStorageService;
import com.diboot.file.util.FileHelper;
import com.diboot.file.util.ImageHelper;
import com.diboot.iam.annotation.BindPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文件 相关Controller
 *
 * @author MyName
 * @version 1.0
 * @date 2022-05-30
 * Copyright © MyCompany
 */
@Slf4j
@RestController
@RequestMapping("/file")
@BindPermission(name = "文件", code = "File")
public class FileController {
    @Autowired
    private FileRecordService fileRecordService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件记录
     * @throws Exception
     */
    @PostMapping("/upload")
    public JsonResult<FileRecord> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (!FileHelper.isValidFileExt(file.getOriginalFilename())) {
            log.warn("非法的文件上传:{}", file.getOriginalFilename());
            return JsonResult.FAIL_VALIDATION("非法的文件上传");
        }
        FileRecord fileRecord = fileStorageService.save(file);
        fileRecordService.createEntity(fileRecord);
        return JsonResult.OK(fileRecord);
    }

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 结果集
     */
    @PostMapping(value = "/batch-upload")
    public JsonResult<?> batchUploadFile(@RequestParam("files") MultipartFile[] files) {
        List<String> errFiles = new ArrayList<>();
        List<FileRecord> fileRecords = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                FileRecord fileRecord = fileStorageService.save(file);
                fileRecords.add(fileRecord);
            } catch (Exception e) {
                String filename = file.getOriginalFilename();
                log.warn("保存文件 {} 失败", filename, e);
                errFiles.add(filename);
            }
        }
        if (!fileRecords.isEmpty()) {
            fileRecordService.createEntities(fileRecords);
        }
        return JsonResult.OK(new HashMap<String, Object>() {{
            put("errFiles", errFiles);
            put("fileRecords", fileRecords);
        }});
    }

    /**
     * 获取文件
     *
     * @param fileId   文件ID
     * @param response 响应
     * @return
     * @throws Exception
     */
    @GetMapping("/{fileId}")
    public JsonResult<?> read(@PathVariable String fileId, HttpServletResponse response) throws Exception {
        if (S.contains(fileId, ".")) {
            fileId = S.substringBefore(fileId, ".");
        }
        FileRecord fileRecord = fileRecordService.getEntity(fileId);
        if (fileRecord == null) {
            log.warn("文件不存在:{}", fileId);
            return new JsonResult<>(Status.FAIL_VALIDATION, "文件不存在");
        }
        fileStorageService.download(fileRecord, response);
        return null;
    }

    /**
     * 批量下载文件
     *
     * @param fileIds
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping
    public JsonResult<?> batchDownload(@RequestBody List<String> fileIds, HttpServletResponse response) throws Exception {
        List<FileRecord> fileRecords = fileRecordService.getEntityListByIds(fileIds);
        if (V.isEmpty(fileRecords)) {
            log.warn("文件不存在:{}", fileIds);
            return new JsonResult<>(Status.FAIL_VALIDATION, "文件不存在");
        }
//        fileStorageService.download(fileRecord, response);
        return JsonResult.FAIL_VALIDATION("批量下载未实现");
    }

    /**
     * 获取图片文件
     *
     * @param fileId   文件ID
     * @param response 响应
     * @return
     * @throws Exception
     */
    @GetMapping("/{fileId}/image")
    public JsonResult<?> readImage(@PathVariable String fileId, HttpServletResponse response) throws Exception {
        if (S.contains(fileId, ".")) {
            fileId = S.substringBefore(fileId, ".");
        }
        FileRecord fileRecord = fileRecordService.getEntity(fileId);
        if (fileRecord == null) {
            log.warn("文件不存在:{}", fileId);
            return JsonResult.FAIL_VALIDATION("文件不存在");
        }
        if (!ImageHelper.isImage(fileRecord.getFileType())) {
            log.warn("非图片文件:{}", fileId);
            return JsonResult.FAIL_VALIDATION("非图片文件");
        }
        fileStorageService.download(fileRecord, response);
        return null;
    }
}