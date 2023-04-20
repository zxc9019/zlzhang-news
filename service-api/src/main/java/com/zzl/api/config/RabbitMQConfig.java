package com.zzl.api.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_ARTICLE = "exchange_article";

    public static final String QUEUE_ARTICLE = "queue_article";

    //创建交换机
    @Bean(EXCHANGE_ARTICLE)
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_ARTICLE).durable(true).build();
    }

    //队列
    @Bean(QUEUE_ARTICLE)
    public Queue queue() {
        return new Queue(QUEUE_ARTICLE);
    }

    //绑定交换机
    @Bean
    public Binding binding(@Qualifier(EXCHANGE_ARTICLE) Exchange exchange, @Qualifier(QUEUE_ARTICLE) Queue queue) {
        // 执行绑定
        return BindingBuilder.bind(queue).to(exchange).with("article.*").noargs();
    }


}
