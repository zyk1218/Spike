package com.imooc.miaosha.rabbitmq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;



@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);



    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        logger.info("receive message:"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEQUE_1)
    public void topicReceive_1(String message){
        logger.info("topicReceive_1 message:"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEQUE_2)
    public void topicReceive_2(String message){
        logger.info("topicReceive_2 message:"+message);
    }

    @RabbitListener(queues = MQConfig.HEADER_QUEQUE)
    public void HeaderReceive(byte[] message){
        logger.info("headerReceive message:"+new String(message));
    }
}
