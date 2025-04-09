package com.liuxu.behavior.controller;

import com.liuxu.behavior.service.AppBehaviorEntryService;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/behavior_entry")
public class AppBehaviorEntryController {
    @Autowired
    private AppBehaviorEntryService appBehaviorEntryService;

    @GetMapping("/one")
    public ResponseResult<AppBehaviorEntry> findByUserIdOrEquipmentId(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId) {

        AppBehaviorEntry appBehaviorEntry = appBehaviorEntryService.findByUserIdOrEquipmentId(userId, equipmentId);
        return ResponseResult.successResult(appBehaviorEntry);
    }
}