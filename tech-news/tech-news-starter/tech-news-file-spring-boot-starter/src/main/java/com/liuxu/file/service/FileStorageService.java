package com.liuxu.file.service;

import java.io.InputStream;
import java.util.List;

/**
 * 文件上传接口
 */
public interface FileStorageService {

    /**
     * @param prefix      文件上传前缀
     * @param fileName    文件名称
     * @param inputStream 文件流
     * @return pathUrl 全路径
     * 图片上传
     */
    String uploadImage(String prefix, String fileName, InputStream inputStream);


    /**
     * @param prefix      文件上传前缀
     * @param fileName    文件名称
     * @param contentType 文件类型 "image/jpg"等
     * @param inputStream 文件流
     * @return pathUrl 全路径
     * 文件上传
     */
    String uploadFile(String prefix, String fileName, String contentType, InputStream inputStream);

    /**
     * @param pathUrl 全路径
     */
    void delete(String pathUrl);


    /**
     * @param pathUrls 全路径集合
     */
    void deleteBatch(List<String> pathUrls);

    /**
     * @param pathUrl 全路径
     * @return
     * 下载文件
     */
    InputStream downloadFile(String pathUrl);

}
