package com.liuxu.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuxu.model.article.pojos.AppAuthor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthorMapper extends BaseMapper<AppAuthor> {
}
