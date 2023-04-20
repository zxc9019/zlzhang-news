package com.zzl.api.service;

import com.github.pagehelper.PageInfo;
import com.zzl.utils.PagedGridResult;

import java.util.List;

public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(list);
        gridResult.setPage(page);
        gridResult.setRecords(pageList.getPages());
        gridResult.setTotal(pageList.getTotal());
        return gridResult;
    }

}
