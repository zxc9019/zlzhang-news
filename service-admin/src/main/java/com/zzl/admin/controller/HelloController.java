package com.zzl.admin.controller;

import com.zzl.api.controller.user.HelloControllerApi;
import com.zzl.grace.result.MyJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerApi {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    //测试
    @Override
    public Object hello() {
        return MyJSONResult.ok();
    }
}
