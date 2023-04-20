package com.zzl.api.controller.user;

import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.RegistLoginBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "用户注册登录", tags = {"用户注册登录"})
@RequestMapping("/passport")
public interface PassportControllerApi {

    @ApiOperation(value = "获得短信验证码", notes = "根据手机号获得短信验证码", httpMethod = "GET")
    @GetMapping("/getSMSCode")
    MyJSONResult getSMSCode(@RequestParam String mobile, HttpServletRequest request);

    @ApiOperation(value = "注册/登录", notes = "注册/登录", httpMethod = "POST")
    @PostMapping("/doLogin")
    MyJSONResult doLogin(@RequestBody @Valid RegistLoginBO registLoginBO, BindingResult result, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation(value = "退出当前账号", notes = "退出当前账号", httpMethod = "POST")
    @PostMapping("/logout")
    MyJSONResult logout(@RequestParam Long userId, HttpServletRequest request, HttpServletResponse response);

}


