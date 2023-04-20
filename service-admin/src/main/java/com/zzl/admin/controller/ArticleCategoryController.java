package com.zzl.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zzl.admin.service.ArticleCategoryService;
import com.zzl.api.BaseController;
import com.zzl.api.controller.admin.ArticleCategoryControllerApi;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.Category;
import com.zzl.pojo.bo.CategoryBO;
import com.zzl.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
public class ArticleCategoryController extends BaseController implements ArticleCategoryControllerApi {

    final static Logger logger = LoggerFactory.getLogger(ArticleCategoryController.class);

    @Autowired
    private ArticleCategoryService categoryService;


    @Override
    public MyJSONResult addNewCategory(CategoryBO categoryBO, BindingResult result) {

        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }

        if (categoryService.queryCategoryNameIsExist(categoryBO.getName()))
            return MyJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);

        redis.del(REDIS_ALL_CATEGORY);
        categoryService.addNewCategory(categoryBO);

        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult delCategory(Integer categoryId) {

        categoryService.delCategory(categoryId);

        redis.del(REDIS_ALL_CATEGORY);
        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult updateCategory(CategoryBO categoryBO, BindingResult result) {
        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }

        //查重
        if (categoryService.queryCategoryNameIsExist(categoryBO.getName()))
            return MyJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);

        categoryService.updateCategory(categoryBO);
        redis.del(REDIS_ALL_CATEGORY);
        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult getCategoryList() throws JsonProcessingException {
        String category = redis.get(REDIS_ALL_CATEGORY);
        if (StringUtils.isNotBlank(category)) {

            /*List<Category> categoryList = new ObjectMapper().readValue(category, new TypeReference<List<Category>>() {
            });*/
            List<Category> categoryList = JsonUtils.jsonToList(category, Category.class);

            return MyJSONResult.ok(categoryList);
        }
        List<Category> categoryList = categoryService.getCategoryList();
        redis.set(REDIS_ALL_CATEGORY, JsonUtils.objectToJson(categoryList));
        return MyJSONResult.ok(categoryList);
    }

}


