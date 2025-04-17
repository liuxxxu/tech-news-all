package com.liuxu.wemedia.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.feigns.AdminFeign;
import com.liuxu.model.admin.pojo.AdChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class WmChannelsController {

    @Autowired
    private AdminFeign adminFeign;

    @GetMapping("/all")
    public ResponseResult<List<AdChannel>> list() {
        return adminFeign.selectChannels();
    }
}
