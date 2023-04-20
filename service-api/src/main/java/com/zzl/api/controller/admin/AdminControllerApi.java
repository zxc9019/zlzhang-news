package com.zzl.api.controller.admin;

import com.zzl.grace.result.MyJSONResult;
import com.zzl.pojo.bo.AdminLoginBO;
import com.zzl.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Api(value = "管理员相应接口", tags = {"管理员相应接口"})
@RequestMapping("/admin")
public interface AdminControllerApi {

    @ApiOperation(value = "登录方法接口", notes = "登录方法接口", httpMethod = "POST")
    @PostMapping("/adminLogin")
    MyJSONResult adminLogin(@RequestBody @Valid AdminLoginBO adminLoginBO, BindingResult result, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation(value = "admin账户用户名查重", notes = "admin账户用户名查重", httpMethod = "GET")
    @GetMapping("/adminIsExist")
    MyJSONResult adminIsExist(@RequestParam String username);

    @ApiOperation(value = "新建admin账户", notes = "新建admin账户", httpMethod = "POST")
    @PostMapping("/addNewAdmin")
    MyJSONResult addNewAdmin(@RequestBody @Valid NewAdminBO newAdminBO, BindingResult result, HttpServletRequest request, HttpServletResponse response);

    @ApiOperation(value = "获取admin列表", notes = "获取admin列表", httpMethod = "GET")
    @GetMapping("/getAdminList")
    MyJSONResult getAdminList(@RequestParam Integer page, @RequestParam Integer pageSize);
}

