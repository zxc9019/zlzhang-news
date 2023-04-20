package com.zzl.user.controller;

import com.zzl.api.controller.user.HelloControllerApi;
import com.zzl.grace.result.MyJSONResult;
import com.zzl.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerApi {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private RedisOperator redis;

    //测试
    @Override
    public Object hello() {
        logger.warn("warn: hello~");
        logger.error("error: hello~");

        return MyJSONResult.ok();
    }

    //redis测试
    @GetMapping("/redis")
    public Object redis() {
        return null;
    }
}
