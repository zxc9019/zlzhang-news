package com.zzl.user.controller;


import com.zzl.api.BaseController;
import com.zzl.api.controller.user.PassportControllerApi;
import com.zzl.enums.UserStatus;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.grace.result.ResponseStatusEnum;
import com.zzl.pojo.AppUser;
import com.zzl.pojo.bo.RegistLoginBO;
import com.zzl.user.service.UserService;
import com.zzl.utils.IPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@RestController
public class PassportController extends BaseController implements PassportControllerApi {

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private UserService userService;

    @Override
    public MyJSONResult getSMSCode(String mobile, HttpServletRequest request) {

        //获取ip
        String userIp = IPUtil.getRequestIp(request);

        //判断60秒内是否发送
        /*if (redis.get(MOBILE_SMSCODE + ":" + userIp) != null){
            return GraceJSONResult.errorCustom(ResponseStatusEnum.SMS_NEED_WAIT_ERROR);
        }*/

        //60秒限制
        redis.setnx60s(MOBILE_SMSCODE + ":" + userIp, userIp);

        // 生成100000到999999之间的随机数
        String random = new Random().nextInt(900000) + 100000 + "";
        System.out.println(random);

        //验证码存入redis
        redis.set(MOBILE_SMSCODE + ":" + mobile, random, 5 * 60);

        return MyJSONResult.ok(random);
    }

    @Override
    public MyJSONResult doLogin(RegistLoginBO registLoginBO, BindingResult result, HttpServletRequest request, HttpServletResponse response) {

        //判断 BindingResult验证信息
        if (result.hasErrors()) {
            Map<String, String> map = getErrors(result);
            return MyJSONResult.errorMap(map);
        }

        String mobile = registLoginBO.getMobile();
        String smsCode = registLoginBO.getSmsCode();

        //校验验证码是否匹配
        String redisSMSCode = redis.get(MOBILE_SMSCODE + ":" + mobile);

        if (redisSMSCode == null || !redisSMSCode.equals(smsCode)) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.SMS_CODE_ERROR);
        }

        //判断是否注册
        AppUser user = userService.queryMobileIsExist(mobile);

        //如果账号被冻结 禁止登录
        if (user != null && user.getActiveStatus() == UserStatus.FROZEN.type) {
            return MyJSONResult.errorCustom(ResponseStatusEnum.USER_FROZEN);
        }

        //未注册则注册
        if (user == null) {
            user = userService.createUser(mobile);
        }

        //设置会话和cookie
        int userActiveStatus = user.getActiveStatus();
        if (userActiveStatus != UserStatus.FROZEN.type) {
            String uToken = UUID.randomUUID().toString();
            redis.set(REDIS_USER_TOKEN + ":" + user.getId(), uToken, 7 * 24 * 60 * 60);

            //保存token到cookie中
            setCookie(request, response, "utoken", uToken, COOKIE_MONTH);
            setCookie(request, response, "uid", user.getId().toString(), COOKIE_MONTH);
        }

        //销毁redis验证码
        redis.del(MOBILE_SMSCODE + ":" + mobile);

        return MyJSONResult.ok(userActiveStatus);
    }

    @Override
    public MyJSONResult logout(Long userId, HttpServletRequest request, HttpServletResponse response) {

        redis.del(REDIS_USER_TOKEN + ":" + userId);

        setCookie(request, response, "utoken", "", COOKIE_DEL);
        setCookie(request, response, "uid", "", COOKIE_DEL);

        return MyJSONResult.ok();
    }


}
