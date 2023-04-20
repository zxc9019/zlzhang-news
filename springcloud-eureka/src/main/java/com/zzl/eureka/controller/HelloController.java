package com.zzl.eureka.controller;

import com.zzl.api.controller.user.HelloControllerApi;
import com.zzl.grace.result.MyJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController implements HelloControllerApi {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);


    @Override
    @GetMapping("/hello")
    public Object hello() {
        return MyJSONResult.ok();
    }

}
