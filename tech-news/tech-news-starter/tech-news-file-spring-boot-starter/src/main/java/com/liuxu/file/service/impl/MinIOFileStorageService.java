package com.liuxu.file.service.impl;


import com.liuxu.file.config.MinIOConfig;
import com.liuxu.file.config.MinIOConfigProperties;
import com.liuxu.file.service.FileStorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@EnableConfigurationProperties(MinIOConfigProperties.class)
@Import(MinIOConfig.class)
@Component
public class MinIOFileStorageService implements FileStorageService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinIOConfigProperties minIOConfigProperties;

    private final static String separator = "/";

    /**
     * @param prefix      文件上传前缀
     * @param fileName    文件名称
     * @param inputStream 文件流
     * @return pathUrl 全路径
     * 文件上传
     */
    @Override
    public String uploadImage(String prefix, String fileName, InputStream inputStream) {
        return this.uploadFile(prefix, fileName, "image/jpg", inputStream);
    }

    /**
     * 上传图片文件
     *
     * @param prefix      文件前缀
     * @param fileName    文件名
     * @param contentType 文件类型 图片："image/jpg"
     * @param inputStream 文件流
     * @return 文件全路径
     */
    @Override
    public String uploadFile(String prefix, String fileName, String contentType, InputStream inputStream) {
        String filePath = builderFilePath(prefix, fileName);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType(contentType)
                    .bucket(minIOConfigProperties.getBucket()).stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            StringBuilder urlPath = new StringBuilder();
            urlPath.append(minIOConfigProperties.getEndpoint());
            urlPath.append(minIOConfigProperties.getBucket());
            urlPath.append(separator);
            urlPath.append(filePath);
            return urlPath.toString();
        } catch (Exception ex) {
            log.error("minio上传文件失败. prefix:{}, fileName:{}", prefix, fileName, ex);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * @param dirPath  文件夹路径
     * @param fileName yyyy/mm/dd/file.jpg
     */
    public String builderFilePath(String dirPath, String fileName) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if (StringUtils.hasText(dirPath)) {
            stringBuilder.append(dirPath).append(separator);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(separator);
        // 获取后缀名
        int index = fileName.lastIndexOf(".");
        String suffix = fileName.substring(index);
        // 判断格式
        if (!suffix.equals(".jpg") && !suffix.equals(".jpeg") && !suffix.equals(".png")) {
            throw new RuntimeException("文件格式不支持");
        }
        String realName = fileName.substring(0, index);
        // 文件名加上时间戳防止重复
        String timestamp = String.valueOf(System.currentTimeMillis());
        stringBuilder.append(realName).append("_").append(timestamp).append(suffix);
        return stringBuilder.toString();
    }

    /**
     * 删除文件
     *
     * @param pathUrl 文件全路径
     */
    @Override
    public void delete(String pathUrl) {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint(), "");
        int index = key.indexOf(separator);
        String bucket = key.substring(0, index);
        String filePath = key.substring(index + 1);
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("minio remove file error.  pathUrl:{}", pathUrl);
            e.printStackTrace();
        }
    }

    /**
     * @param pathUrls 全路径集合
     * @throws Exception 批量文件删除
     */
    @Override
    public void deleteBatch(List<String> pathUrls) {

        List<DeleteObject> objects = new LinkedList<>();
        for (String pathUrl : pathUrls) {
            String key = pathUrl.replace(minIOConfigProperties.getEndpoint(), "");
            int index = key.indexOf(separator);
//            String bucket = key.substring(0,index);
            String filePath = key.substring(index + 1);
            objects.add(new DeleteObject(filePath));
        }

        try {
            minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(minIOConfigProperties.getBucket())
                            .objects(objects).build());
        } catch (Exception e) {
            log.error("minio remove file error.");
            e.printStackTrace();
        }

    }

    /**
     * 下载文件
     *
     * @param pathUrl 文件全路径
     * @return 文件流
     */
    @Override
    public InputStream downloadFile(String pathUrl) {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint(), "");
        int index = key.indexOf(separator);
        String bucket = key.substring(0, index);
        String filePath = key.substring(index + 1);
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minIOConfigProperties.getBucket()).object(filePath).build());
        } catch (Exception e) {
            log.error("minio down file error.  pathUrl:{}", pathUrl);
            e.printStackTrace();
        }

        return inputStream;
    }
}
