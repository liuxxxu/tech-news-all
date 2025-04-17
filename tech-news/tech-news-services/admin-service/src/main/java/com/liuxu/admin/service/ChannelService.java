package com.liuxu.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.model.admin.dtos.ChannelDTO;
import com.liuxu.model.admin.pojo.AdChannel;
import com.liuxu.common.dtos.ResponseResult;

/**
 * 频道管理 */
public interface ChannelService extends IService<AdChannel> {
    /**
     * 根据名称分页查询频道列表
     * @param dto
     * @return
     */
    public ResponseResult findByName(ChannelDTO dto);

    /**
     * 新增
     * @param channel
     * @return
     */
    public ResponseResult insert(AdChannel channel);

    /**
     * 修改
     * @param adChannel
     * @return
     */
    public ResponseResult update(AdChannel adChannel);

    /**
     * 删除
     * @param id
     * @return
     */
    public ResponseResult deleteById(Long id);
}
