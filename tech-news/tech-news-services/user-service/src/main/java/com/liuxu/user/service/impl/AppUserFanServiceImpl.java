package com.liuxu.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.model.user.pojos.AppUserFan;
import com.liuxu.user.service.AppUserFanService;
import com.liuxu.user.mapper.AppUserFanMapper;
import org.springframework.stereotype.Service;


@Service
public class AppUserFanServiceImpl extends ServiceImpl<AppUserFanMapper, AppUserFan>
    implements AppUserFanService{

}




