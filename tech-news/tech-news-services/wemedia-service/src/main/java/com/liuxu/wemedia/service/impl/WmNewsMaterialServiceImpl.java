package com.liuxu.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.wemedia.pojos.WmNewsMaterial;
import com.liuxu.wemedia.mapper.WmNewsMaterialMapper;
import com.liuxu.wemedia.service.WmNewsMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WmNewsMaterialServiceImpl extends ServiceImpl<WmNewsMaterialMapper, WmNewsMaterial> implements WmNewsMaterialService {

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 根据图片id查询引用数量
     * @param id
     * @return
     */
    @Override
    public Integer countNewsBymaterialId(Long id) {
        if (id == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID,"图片id不能为空");
        }
        LambdaQueryWrapper<WmNewsMaterial> query = Wrappers.<WmNewsMaterial>lambdaQuery();
        query.eq(WmNewsMaterial::getMaterialId, id);
        return Math.toIntExact(count(query));
    }
}
