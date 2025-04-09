package com.liuxu.wemedia.controller;

import com.liuxu.common.constants.wemedia.WemediaConstants;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.wemedia.dtos.WmMaterialDTO;
import com.liuxu.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/material")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    @PostMapping("/upload")
    public ResponseResult<String> upload(@RequestParam("prefix") String prefix,
                                             @RequestParam("fileName") String fileName,
                                             @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseResult.successResult(wmMaterialService.upload(prefix, fileName, file.getInputStream()));
    }

    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmMaterialDTO dto) {
        return wmMaterialService.findList(dto);
    }

    @GetMapping("/delete/{id}")
    public ResponseResult delPicture(@PathVariable("id") Long id) {
        return wmMaterialService.delPicture(id);
    }

    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancelCollectionMaterial(@PathVariable("id") Long id) {
        return wmMaterialService.updateStatus(id, WemediaConstants.CANCEL_COLLECT_MATERIAL);
    }

    @GetMapping("/collect/{id}")
    public ResponseResult collectionMaterial(@PathVariable("id") Long id) {
        return wmMaterialService.updateStatus(id, WemediaConstants.COLLECT_MATERIAL);
    }
}
