package com.zzl.article.controller;

import com.zzl.api.config.RabbitMQConfig;
import com.zzl.api.config.RabbitMQDelayConfig;
import com.zzl.grace.result.MyJSONResult;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@Api(value = "功能测试", tags = {"功能测试"})
@RequestMapping("/test")
public class HelloController {

    final static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //测试
    @GetMapping("/in")
    public Object in(@RequestParam Object ob) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE, "article.hello", ob);
        return MyJSONResult.ok();
    }

    @GetMapping("/out")
    public Object out() {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_ARTICLE, "article.hello", "测试消息");
        return MyJSONResult.ok();
    }

    //测试
    @GetMapping("/delay")
    public Object delay() {

        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置消息持久
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                //设置消息延迟时间，单位ms
                message.getMessageProperties().setDelay(5000);
                return message;
            }
        };

        rabbitTemplate.convertAndSend(RabbitMQDelayConfig.EXCHANGE_DELAY, "delay.hello", "延迟消息测试", messagePostProcessor);

        System.out.println("mq延迟消息：" + new Date());

        return MyJSONResult.ok();
    }

    @GetMapping("/zuul")
    public Object zuul() {
        return MyJSONResult.ok();
    }


}
