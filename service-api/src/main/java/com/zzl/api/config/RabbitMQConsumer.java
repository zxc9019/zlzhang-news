package com.zzl.api.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_ARTICLE})
    public void watchQueue(String payload,Message message){
        System.out.println(payload);

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        if (routingKey.equalsIgnoreCase("article.*")){
            System.out.println("exchange_article");
        }else{
            System.out.println("=========");
        }

    }

}
