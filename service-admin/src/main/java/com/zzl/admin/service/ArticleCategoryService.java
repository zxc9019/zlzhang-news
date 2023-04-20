package com.zzl.admin.service;

import com.zzl.pojo.Category;
import com.zzl.pojo.bo.CategoryBO;

import java.util.List;

public interface ArticleCategoryService {

    void addNewCategory(CategoryBO categoryBO);

    void delCategory(Integer categoryId);

    void  updateCategory(CategoryBO categoryBO);

    List<Category> getCategoryList();

    //查重
    Boolean queryCategoryNameIsExist(String categoryName);

}
