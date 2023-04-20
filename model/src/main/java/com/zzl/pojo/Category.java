package com.zzl.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "category")
public class Category {
    @Id
    private Integer id;

    //分类名
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}