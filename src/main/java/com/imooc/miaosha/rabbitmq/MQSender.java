package com.imooc.miaosha.rabbitmq;


import com.imooc.miaosha.redis.RedisService;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;




    public void sendMiaoshaMessage(MiaoshaMessage message) {
        String msg = RedisService.beanToString(message);
        logger.info("send miaoshamessage:"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHAQUEUE,msg);
    }
//
//
//    public void send(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
//    }
//
//    public void sendTopic(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key.1",msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.#",msg+"2");
//    }
//    public void sendFanout(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
//    }
//
//
//    public void sendHeader(Object message){
//        String msg = RedisService.beanToString(message);
//        logger.info("send message:"+msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1","value1");
//        properties.setHeader("header2","value2");
//        Message obj = new Message(msg.getBytes(),properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
//    }

}
