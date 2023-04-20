package com.zzl.article.service;

import com.zzl.pojo.Category;
import com.zzl.pojo.bo.CategoryBO;
import com.zzl.pojo.bo.NewArticleBO;
import com.zzl.pojo.vo.ArticleDetailVO;
import com.zzl.utils.PagedGridResult;

import java.util.Date;
import java.util.List;

public interface ArticleService {

    //发布文章
    void addNewArticle(NewArticleBO articleBO,Category category);

    //发布定时文章
    void updateAppointToPublish();

    //定时任务修改文章状态
    void updateArticleToPublish(Long articleId);

    //查询我的文章列表
    PagedGridResult getMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize);

    //更改文章的状态
    void updateArticleStatus(String articleId, Integer pendingStatus);

    //管理员查询所有文章列表
    PagedGridResult getAllList(Integer status, Integer page, Integer pageSize);

    void delArticle(Long articleId);

    void updateArticle(NewArticleBO articleBO);

    //首页文章列表
    PagedGridResult getIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize);

    //查询文章详情
    ArticleDetailVO getDetail(Long articleId);
}
