package com.zzl.admin.service;

import com.zzl.pojo.AdminUser;
import com.zzl.pojo.bo.NewAdminBO;
import com.zzl.utils.PagedGridResult;

public interface AdminUserService {

    //获取管理员用户信息
    AdminUser queryAdminByUsername(String username);

    //新增管理员账号
    void createAdminUser(NewAdminBO newAdminBO);

    //获取管理员列表
    PagedGridResult queryAdminList(Integer page, Integer pageSize);

}
