package com.zzl.user.controller;

import com.zzl.api.BaseController;
import com.zzl.api.controller.user.UserControllerApi;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.AppUser;
import com.zzl.pojo.bo.UpdateUserInfoBO;
import com.zzl.pojo.vo.AppUserVO;
import com.zzl.pojo.vo.UserAccountInfoVO;
import com.zzl.user.service.UserService;
import com.zzl.utils.JsonUtils;
import com.zzl.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class UserController extends BaseController implements UserControllerApi {

    final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Override
    public MyJSONResult getUserInfo(Long userId) {
        //判断参数是否为0
        if (userId == 0) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        //查询用户信息
        AppUser user = getUser(userId);

        AppUserVO appUserVO = new AppUserVO();
        BeanUtils.copyProperties(user, appUserVO);

        return MyJSONResult.ok(appUserVO);
    }

    @Override
    public MyJSONResult getAccountInfo(Long userId) {

        //判断参数是否为空
        if (userId == 0) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.UN_LOGIN);
        }

        //查询用户信息
        AppUser user = getUser(userId);

        UserAccountInfoVO accountInfoVO = new UserAccountInfoVO();
        BeanUtils.copyProperties(user, accountInfoVO);

        return MyJSONResult.ok(accountInfoVO);
    }

    @Override
    public MyJSONResult updateUserInfo(UpdateUserInfoBO updateUserInfoBO, BindingResult result) {

        // 判断验证信息
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return MyJSONResult.errorMap(errorMap);
        }

        userService.updateUserInfo(updateUserInfoBO);

        return MyJSONResult.ok();
    }

    @Override
    public MyJSONResult getUserList(String nickname, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {

        if (page == null) {
            page = PAGE;
        }

        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedGridResult userList = userService.getUserList(nickname, status, startDate, endDate, page, pageSize);

        return MyJSONResult.ok(userList);
    }

    private AppUser getUser(Long userId) {
        //查询redis是否存在信息
        String userJson = redis.get(REDIS_USER_INFO + ":" + userId);

        AppUser user = null;
        if (StringUtils.isNotBlank(userJson)) {
            user = JsonUtils.jsonToPojo(userJson, AppUser.class);
        } else {
            user = userService.getUser(userId);
            //用户信息存入redis
            redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user), 30 * 24 * 60 * 60);
        }

        return user;
    }

    @Override
    public MyJSONResult getByIdsList(String userIds) {

        System.out.println("userIds:" + userIds);

        if (StringUtils.isBlank(userIds)) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        List<AppUserVO> publisherList = new ArrayList<>();
        List<String> userIdList = JsonUtils.jsonToList(userIds, String.class);

        for (String s : userIdList) {

            AppUserVO userVO = getBasicUserInfo(s);
            publisherList.add(userVO);
        }

        return MyJSONResult.ok(publisherList);
    }

    private AppUserVO getBasicUserInfo(String userId) {
        AppUser user = getUser(Long.parseLong(userId));

        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }
}
