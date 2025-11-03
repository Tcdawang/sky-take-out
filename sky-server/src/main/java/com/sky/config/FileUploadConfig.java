package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件上传的配置类
 */
@Configuration
public class FileUploadConfig {
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        AliOssUtil aliOssUtil = new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
        return aliOssUtil;
    }
}
