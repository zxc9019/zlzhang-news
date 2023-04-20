package com.zzl.admin.service.Impl;

import com.zzl.admin.mapper.CategoryMapper;
import com.zzl.admin.service.ArticleCategoryService;
import com.zzl.exception.MyException;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.Category;
import com.zzl.pojo.bo.CategoryBO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void addNewCategory(CategoryBO categoryBO) {

        Category category = new Category();
        BeanUtils.copyProperties(categoryBO, category);
        int result = categoryMapper.insert(category);
        if (result != 1) {
            MyException.display(ResponseStatusEnum.CATEGORY_ERROR);
        }

    }

    @Override
    @Transactional
    public void delCategory(Integer categoryId) {
        int result = categoryMapper.deleteByPrimaryKey(categoryId);
        if (result == 0)
            MyException.display(ResponseStatusEnum.CATEGORY_DELETE_ERROR);
    }

    @Override
    @Transactional
    public void updateCategory(CategoryBO categoryBO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryBO, category);
        int result = categoryMapper.updateByPrimaryKeySelective(category);
        if (result == 0)
            MyException.display(ResponseStatusEnum.CATEGORY_UPDATE_ERROR);
    }

    @Override
    public List<Category> getCategoryList() {
        List<Category> categoryList = categoryMapper.selectAll();
        return categoryList;
    }

    @Override
    public Boolean queryCategoryNameIsExist(String categoryName) {
        Example userExample = new Example(Category.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("name", categoryName);
        Category category = categoryMapper.selectOneByExample(userExample);
        if (category != null)
            return true;

        return false;
    }
}
