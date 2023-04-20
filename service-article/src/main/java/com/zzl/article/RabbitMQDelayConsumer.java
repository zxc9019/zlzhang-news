package com.zzl.article;

import com.zzl.api.config.RabbitMQDelayConfig;
import com.zzl.article.service.ArticleService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQDelayConsumer {

    @Autowired
    private ArticleService articleService;

    @RabbitListener(queues = {RabbitMQDelayConfig.QUEUE_DELAY})
    public void watchQueue(Long payload, Message message) {

        System.out.println(payload);

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        System.out.println(routingKey);

        //接收定时发布，修改文章状态
        Long articleId = payload;
        articleService.updateArticleToPublish(articleId);

    }

}
