package com.travelfamilies.tools;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Component
public class AliOSSUtils {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    public String upload(MultipartFile file) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 避免文件覆盖，使用UUID生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));

        // 上传文件到OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        // 文件访问路径 (存储到数据库的 URL)
        String url = "https://" + bucketName + "." + endpoint + "/" + fileName;

        // 关闭ossClient
        ossClient.shutdown();
        return url;
    }
}
