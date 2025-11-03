package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "公共接口")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> uploadFile(MultipartFile file){
        log.info("上传的文件名:{}", file);
        String originalFilename = file.getOriginalFilename();//获取文件的原始名
        log.info("文件的原始名:{}", originalFilename);
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));//获取文件的后缀名
        log.info("文件的后缀名:{}", extension);
        String objectName = UUID.randomUUID().toString() + extension;//构建新文件名称
        log.info("新文件名称:{}", objectName);
        try {
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            log.info("图片的访问地址为:{}",filePath);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
