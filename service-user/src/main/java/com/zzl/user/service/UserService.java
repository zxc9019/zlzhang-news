package com.zzl.user.service;

import com.zzl.pojo.AppUser;
import com.zzl.pojo.bo.UpdateUserInfoBO;
import com.zzl.utils.PagedGridResult;

import java.util.Date;

public interface UserService {

    //判断用户是否存在
    AppUser queryMobileIsExist(String mobile);

    //创建用户
    AppUser createUser(String mobile);

    //查询用户信息
    AppUser getUser(Long userId);

    //用户更新资料 状态改激活
    void updateUserInfo(UpdateUserInfoBO updateUserInfoBO);

    //获取用户列表
    PagedGridResult getUserList(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize);

}
