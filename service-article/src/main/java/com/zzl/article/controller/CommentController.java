package com.zzl.article.controller;

import com.zzl.api.BaseController;
import com.zzl.api.controller.article.CommentControllerApi;
import com.zzl.article.service.CommentService;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.CommentReplyBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;

@RestController
public class CommentController extends BaseController implements CommentControllerApi {

    final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Override
    public MyJSONResult createComment(CommentReplyBO commentReplyBO, BindingResult result) {

        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }

        //根据id查询用户昵称存入表中
        String userId = commentReplyBO.getCommentUserId();

        //调用用户服务
        HashSet<String> idSet = new HashSet<>();
        String nickname = getBasicUserList(idSet).get(0).getNickname();

        //保存用户评论
        commentService.createComment(commentReplyBO.getArticleId(), commentReplyBO.getFatherId(), commentReplyBO.getContent(), userId, nickname);


        return MyJSONResult.ok();
    }

}
