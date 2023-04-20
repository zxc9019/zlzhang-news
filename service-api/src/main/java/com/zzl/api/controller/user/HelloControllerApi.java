package com.zzl.api.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

@Api(value = "功能测试", tags = {"功能测试"})
public interface HelloControllerApi {

    @ApiOperation(value = "hello方法接口", notes = "hello方法接口", httpMethod = "GET")
    @GetMapping("/hello")
    Object hello();
}
