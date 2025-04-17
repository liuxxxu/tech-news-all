package com.liuxu.admin.controller;

import com.liuxu.admin.service.ChannelService;
import com.liuxu.model.admin.dtos.ChannelDTO;
import com.liuxu.model.admin.pojo.AdChannel;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.validator.ValidatorAdd;
import com.liuxu.common.validator.ValidatorUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/channel")
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody ChannelDTO dto) {
        return channelService.findByName(dto);
    }

    @PostMapping("/save")
    public ResponseResult save(@RequestBody @Validated({ValidatorAdd.class}) AdChannel channel) {
        return channelService.insert(channel);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody @Validated({ValidatorUpdate.class}) AdChannel channel) {
        return channelService.update(channel);
    }

    @GetMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") Long id) {
        return channelService.deleteById(id);
    }

    @GetMapping("/channels")
    public ResponseResult findAll() {
        List<AdChannel> list = channelService.list();
        return ResponseResult.successResult(list);
    }

    @GetMapping("/one/{id}")
    public ResponseResult findOne(@PathVariable Long id) {
        return ResponseResult.successResult(channelService.getById(id));
    }

}
