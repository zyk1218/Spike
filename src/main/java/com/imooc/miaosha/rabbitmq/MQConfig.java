package com.imooc.miaosha.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String MIAOSHAQUEUE = "miaoshaqueue";
    public static final String TOPIC_QUEQUE_1 = "topic.queue.1";
    public static final String TOPIC_QUEQUE_2 = "topic.queue.2";
    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEADER_QUEQUE = "headerQueue";
    public static final String HEADERS_EXCHANGE = "HeadersExchange";


    /**
     * Direct模式，交换机Exchange
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }

    /**
     * Topic模式，交换机Exchange
     */
    @Bean
    public Queue topicQueue_1(){
        return new Queue(TOPIC_QUEQUE_1,true);
    }
    @Bean
    public Queue topicQueue_2(){
        return new Queue(TOPIC_QUEQUE_2,true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBindin_1(){
        return BindingBuilder.bind(topicQueue_1()).to(topicExchange()).with("topic.key.1");
    }
    @Bean
    public Binding topicBindin_2(){
        return BindingBuilder.bind(topicQueue_2()).to(topicExchange()).with("topic");
    }

    /**
     * Fanout模式，交换机Exchange
     */
    @Bean
    public FanoutExchange fandoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding FanoutBindin_1(){
        return BindingBuilder.bind(topicQueue_1()).to(fandoutExchange());
    }
    @Bean
    public Binding FanoutBindin_2(){
        return BindingBuilder.bind(topicQueue_2()).to(fandoutExchange());
    }

    /**
     * Headers模式，交换机Exchange
     */
    @Bean
    public HeadersExchange HeadersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headerQueue(){
        return new Queue(HEADER_QUEQUE,true);
    }

    @Bean
    public Binding HeadersBinding(){
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(headerQueue()).to(HeadersExchange()).whereAll(map).match();
    }


    /**
     * 秒杀Queue
     */
    @Bean
    public Queue miaoshaQueue(){
        return new Queue(MIAOSHAQUEUE,true);
    }

}
