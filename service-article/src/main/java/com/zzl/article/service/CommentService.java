package com.zzl.article.service;

import org.springframework.stereotype.Service;


public interface CommentService {

    //发布评论
    void createComment(Long articleId, String fatherCommentId, String content,
                                  String userId, String nickname);

}
