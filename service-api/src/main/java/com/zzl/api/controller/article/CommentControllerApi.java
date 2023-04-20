package com.zzl.api.controller.article;

import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.CommentReplyBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "评论功能", tags = {"评论功能"})
@RequestMapping("/comment")
public interface CommentControllerApi {

    @ApiOperation(value = "发表评论", notes = "发表评论", httpMethod = "POST")
    @PostMapping("/createComment")
    MyJSONResult createComment(@RequestBody @Valid CommentReplyBO commentReplyBO , BindingResult result);

}
