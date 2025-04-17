package com.liuxu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuxu.model.user.pojos.AppUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {
}
