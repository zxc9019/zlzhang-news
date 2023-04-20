package com.zzl.api.controller.user;

import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.UpdateUserInfoBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

@Api(value = "用户信息相关", tags = {"用户信息相关"})
@RequestMapping("/user")
public interface UserControllerApi {

    @ApiOperation(value = "获取用户基本信息", notes = "获取用户基本信息", httpMethod = "GET")
    @GetMapping("/getUserInfo")
    MyJSONResult getUserInfo(@RequestParam Long userId);

    @ApiOperation(value = "获取用户账户信息", notes = "获取用户账户信息", httpMethod = "GET")
    @GetMapping("/getAccountInfo")
    MyJSONResult getAccountInfo(@RequestParam Long userId);

    @ApiOperation(value = "更新账户资料", notes = "更新账户资料", httpMethod = "PUT")
    @PutMapping("/updateUserInfo")
    MyJSONResult updateUserInfo(@RequestBody @Valid UpdateUserInfoBO updateUserInfoBO, BindingResult result);

    @ApiOperation(value = "获取用户列表", notes = "获取用户列表", httpMethod = "GET")
    @GetMapping("/getUserList")
    MyJSONResult getUserList(@RequestParam String nickname,
                             @RequestParam Integer status,
                             @RequestParam Date startDate,
                             @RequestParam Date endDate,
                             @RequestParam Integer page,
                             @RequestParam Integer pageSize);

    @ApiOperation(value = "根据用户ids查询用户列表", notes = "根据用户ids查询用户列表", httpMethod = "GET")
    @GetMapping("/getByIdsList")
    MyJSONResult getByIdsList(@RequestParam String userIds);
}
