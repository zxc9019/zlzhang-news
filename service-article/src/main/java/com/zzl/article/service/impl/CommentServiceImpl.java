package com.zzl.article.service.impl;

import com.zzl.api.service.BaseService;
import com.zzl.article.mapper.CommentsMapper;
import com.zzl.article.service.ArticleService;
import com.zzl.article.service.CommentService;
import com.zzl.pojo.Comments;
import com.zzl.pojo.vo.ArticleDetailVO;
import com.zzl.utils.PagedGridResult;
import com.zzl.utils.SnowflakeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CommentServiceImpl extends BaseService implements CommentService {

    @Autowired
    private SnowflakeId sid;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentsMapper commentsMapper;

    @Override
    @Transactional
    public void createComment(Long articleId, String fatherCommentId, String content,
                                         String userId, String nickname) {

        ArticleDetailVO article = articleService.getDetail(articleId);

        Comments comments = new Comments();
        comments.setId(sid.nextId());
        comments.setWriterId(article.getPublishUserId());
        comments.setArticleTitle(article.getTitle());
        comments.setArticleCover(article.getCover());
        comments.setArticleId(articleId);
        comments.setFatherId(fatherCommentId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickname);
        comments.setContent(content);
        comments.setCreateTime(new Date());

        commentsMapper.insert(comments);

    }

}


