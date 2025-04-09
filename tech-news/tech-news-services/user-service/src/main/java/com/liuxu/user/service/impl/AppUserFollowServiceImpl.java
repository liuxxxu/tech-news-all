package com.liuxu.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.model.user.pojos.AppUserFollow;
import com.liuxu.user.service.AppUserFollowService;
import com.liuxu.user.mapper.AppUserFollowMapper;
import org.springframework.stereotype.Service;

@Service
public class AppUserFollowServiceImpl extends ServiceImpl<AppUserFollowMapper, AppUserFollow>
    implements AppUserFollowService{

}




