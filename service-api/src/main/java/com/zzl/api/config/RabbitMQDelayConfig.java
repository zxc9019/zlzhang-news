package com.zzl.api.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQDelayConfig {

    public static final String EXCHANGE_DELAY = "EXCHANGE_DELAY";

    public static final String QUEUE_DELAY = "QUEUE_DELAY";

    //创建交换机
    @Bean(EXCHANGE_DELAY)
    public Exchange exchange() {
        //.delayed()设置延迟
        return ExchangeBuilder.topicExchange(EXCHANGE_DELAY).delayed().durable(true).build();
    }

    //队列
    @Bean(QUEUE_DELAY)
    public Queue queue() {
        return new Queue(QUEUE_DELAY);
    }

    //绑定交换机
    @Bean
    public Binding delayBinding(@Qualifier(EXCHANGE_DELAY) Exchange exchange, @Qualifier(QUEUE_DELAY) Queue queue) {
        // 执行绑定
        return BindingBuilder.bind(queue).to(exchange).with("publish.delay.#").noargs();
    }


}
