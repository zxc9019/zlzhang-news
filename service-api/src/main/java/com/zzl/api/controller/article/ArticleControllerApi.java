package com.zzl.api.controller.article;

import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.NewArticleBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@Api(value = "文章功能", tags = {"文章功能"})
@RequestMapping("/article")
public interface ArticleControllerApi {

    @ApiOperation(value = "新增文章", notes = "新增文章", httpMethod = "POST")
    @PostMapping("/addNewArticle")
    MyJSONResult addNewArticle(@RequestBody @Valid NewArticleBO articleBO, BindingResult result);

    @ApiOperation(value = "删除文章", notes = "删除文章", httpMethod = "DELETE")
    @DeleteMapping("/delArticle")
    MyJSONResult delArticle(@RequestParam Long articleId);

    @ApiOperation(value = "更新文章", notes = "更新文章", httpMethod = "PUT")
    @PutMapping("/updateArticle")
    MyJSONResult updateArticle(@RequestBody @Valid NewArticleBO articleBO, BindingResult result);

    @ApiOperation(value = "获取主页文章列表", notes = "获取主页文章列表", httpMethod = "GET")
    @GetMapping("/getArticleList")
    MyJSONResult getArticleList(@ApiParam(name = "keyword", value = "按标题搜索", required = false)
                                   @RequestParam String keyword,
                                @ApiParam(name = "category", value = "文章分类", required = false)
                                   @RequestParam Integer category,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize);

    @ApiOperation(value = "获取个人文章列表", notes = "获取个人文章列表", httpMethod = "GET")
    @GetMapping("/getMyArticleList")
    MyJSONResult getMyArticleList(@RequestParam String userId, @RequestParam String keyword, @RequestParam Integer statu,
                                  @RequestParam Date startDate, @RequestParam Date endDate, @RequestParam Integer page,
                                  @RequestParam Integer pageSize);

    @ApiOperation(value = "管理员查询所有文章列表", notes = "管理员查询所有文章列表", httpMethod = "GET")
    @GetMapping("getAllList")
    MyJSONResult getAllList(@RequestParam Integer status, @RequestParam Integer page, @RequestParam Integer pageSize);

    @ApiOperation(value = "管理员审核文章", notes = "管理员审核文章", httpMethod = "POST")
    @PostMapping("doReview")
    MyJSONResult doReview(@RequestParam String articleId, @RequestParam Integer reviewResult);

    @ApiOperation(value = "文章详情查询", notes = "文章详情查询", httpMethod = "GET")
    @GetMapping("detail")
    MyJSONResult detail(@RequestParam Long articleId);

    @ApiOperation(value = "文章阅读量+1", notes = "文章阅读量+1", httpMethod = "POST")
    @PostMapping("readArticle")
    MyJSONResult readArticle(@RequestParam Long articleId);

}
