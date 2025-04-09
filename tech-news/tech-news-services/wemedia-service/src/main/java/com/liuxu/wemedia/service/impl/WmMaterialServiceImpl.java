package com.liuxu.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.common.constants.wemedia.WemediaConstants;
import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.file.service.impl.MinIOFileStorageService;
import com.liuxu.model.wemedia.dtos.WmMaterialDTO;
import com.liuxu.model.wemedia.pojos.WmMaterial;
import com.liuxu.wemedia.mapper.WmMaterialMapper;
import com.liuxu.wemedia.service.WmMaterialService;
import com.liuxu.wemedia.service.WmNewsMaterialService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private WmNewsMaterialService wmNewsMaterialService;

    @Autowired
    private MinIOFileStorageService minIOFileStorageService;

    /**
     * 上传图片
     * @return
     */
    @Override
    public String upload(String prefix, String fileName, InputStream inputStream) {
        String url = minIOFileStorageService.uploadImage(prefix, fileName, inputStream);
        Long userId = AppThreadLocalUtils.getUserId();
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(userId);
        wmMaterial.setUrl(url);
        wmMaterial.setType((short) 0);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);
        return url;
    }

    /**
     * 素材列表查询
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findList(WmMaterialDTO dto) {
        // 1. 校验参数 分页 参数
        dto.checkParam();
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2. 查询
        // 2.1 分页条件
        Page<WmMaterial> pageReq = new Page<>(dto.getPage(), dto.getSize());

        // 2.2 查询条件 （收藏条件 用户id 发布时间降序）
        LambdaQueryWrapper<WmMaterial> query = Wrappers.<WmMaterial>lambdaQuery();

        // 查询自己上传的素材
        query.eq(WmMaterial::getUserId, userId);

        // 按照发布时间进行降序排序
        query.orderByDesc(WmMaterial::getCreatedTime);

        if (WemediaConstants.COLLECT_MATERIAL.equals(dto.getIsCollection())) {
            // 查询收藏的素材
            query.eq(WmMaterial::getIsCollection, dto.getIsCollection());
        }

        // 3. 执行查询 封装分页结果返回
        IPage<WmMaterial> pageResult = page(pageReq, query);
        List<WmMaterial> records = pageResult.getRecords();
        return new PageResponseResult(dto.getPage(), dto.getSize(), pageResult.getTotal(), records);
    }

    /**
     * 删除图片
     * 
     * @param id
     * @return
     */
    @Override
    public ResponseResult delPicture(Long id) {
        // 1.参数校验
        if (id == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE, "图片id不能为空");
        }

        // 2. 查询当前图片是否存在
        WmMaterial material = getById(id);
        if (material == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "当前图片不存在");
        }

        // 3. 查询当前图片是否被文章引用
        // Integer count = wmNewsMaterialService.countNewsBymaterialId(id);
        // if (count > 0) {
        //     CustException.throwException(AppHttpCodeEnum.DATA_NOT_ALLOW, "当前图片被文章引用不允许删除");
        // }

        // 4. 删除数据库文件
        removeById(id);

        // 5. 删除存储的文件
        // minIOFileStorageService.delete(material.getUrl());

        // 6.返回
        return ResponseResult.successResult();
    }

    /**
     * 收藏与取消收藏
     * 
     * @param id
     * @param isCollection
     * @return
     */
    @Override
    public ResponseResult updateStatus(Long id, Short isCollection) {
        // 1. 检查参数
        if (id == null || isCollection == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2.检查id是否存在
        WmMaterial material = getById(id);
        if (material == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "素材不存在");
        }

        // 3. 判断图片是否属于当前用户
        Long userId = AppThreadLocalUtils.getUserId();
        if (!material.getUserId().equals(userId)) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_ALLOW, "素材不属于你,无法操作");
        }

        material.setIsCollection(isCollection);
        // 4. 更新
        updateById(material);

        // 5. 返回
        return ResponseResult.successResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 检查上传文件后缀
     * 
     * @param originalfileName
     * @return
     */
    private boolean checkFileSuffix(String originalfileName) {
        if (StringUtils.isNotBlank(originalfileName)) {
            List<String> allowSuffix = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
            for (String suffix : allowSuffix) {
                if (originalfileName.endsWith(suffix)) {
                    return true;
                }
            }
        }
        return false;
    }
}
