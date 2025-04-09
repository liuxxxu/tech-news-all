package com.liuxu.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.wemedia.dtos.WmMaterialDTO;
import com.liuxu.model.wemedia.pojos.WmMaterial;

import java.io.InputStream;

public interface WmMaterialService extends IService<WmMaterial> {
    /**
     * 上传图片
     * @return
     */
    String upload(String prefix, String fileName, InputStream inputStream);

    /**
     * 素材列表查询
     * @param dto
     * @return
     */
    ResponseResult findList(WmMaterialDTO dto);

    /**
     * 删除图片
     * @param id
     * @return
     */
    ResponseResult delPicture(Long id);

    /**
     * 收藏与取消收藏
     * @param id
     * @param isCollection
     * @return
     */
    ResponseResult updateStatus(Long id, Short isCollection);
}
