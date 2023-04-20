package com.zzl.article.mapper;

import com.zzl.my.mapper.MyMapper;
import com.zzl.pojo.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapperCustom extends MyMapper<Article> {

    public void updateAppointToPublish();

}