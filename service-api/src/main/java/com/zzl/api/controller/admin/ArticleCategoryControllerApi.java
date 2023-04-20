package com.zzl.api.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.CategoryBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "文章分类", tags = {"文章分类"})
@RequestMapping("/category")
public interface ArticleCategoryControllerApi {

    @ApiOperation(value = "新增类别", notes = "新增类别", httpMethod = "POST")
    @PostMapping("/addNewCategory")
    MyJSONResult addNewCategory(@RequestBody @Valid CategoryBO categoryBO, BindingResult result);

    @ApiOperation(value = "删除类别", notes = "删除类别", httpMethod = "DELETE")
    @DeleteMapping("/delCategory")
    MyJSONResult delCategory(@RequestParam Integer categoryId);

    @ApiOperation(value = "更新类别", notes = "更新类别", httpMethod = "PUT")
    @PutMapping("/updateCategory")
    MyJSONResult updateCategory(@RequestBody @Valid CategoryBO categoryBO, BindingResult result);

    @ApiOperation(value = "获取全部类别", notes = "获取全部类别，存redis", httpMethod = "GET")
    @GetMapping("/getCategoryList")
    MyJSONResult getCategoryList() throws JsonProcessingException;

}
