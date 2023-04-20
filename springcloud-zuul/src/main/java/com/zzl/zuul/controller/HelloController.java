package com.zzl.zuul.controller;

import com.zzl.grace.result.MyJSONResult;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "功能测试", tags = {"功能测试"})
@RequestMapping("/test")
public class HelloController {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/zuul")
    public Object zuul() {
        return MyJSONResult.ok();
    }


}
