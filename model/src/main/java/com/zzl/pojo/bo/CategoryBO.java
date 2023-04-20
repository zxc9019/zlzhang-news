package com.zzl.pojo.bo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class CategoryBO {
    @Min(value = 1, message = "用户ID不能为空")
    private Integer id;
    @NotBlank(message = "新的分类名不能为空")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}