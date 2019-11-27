package com.imooc.miaosha.rabbitmq;


import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class MQReceiver {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;


    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.MIAOSHAQUEUE)
    public void receive(String message){
        logger.info("receive message:"+message);
        MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message,MiaoshaMessage.class);
        long goodsId = miaoshaMessage.getGoodsId();
        MiaoshaUser user = miaoshaMessage.getUser();

        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) return;
        //判断是否秒杀到了（因为需要保证一人仅秒杀一次）
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order != null){
            return ;
        }
        //减库存，下订单，写入秒杀订单
        miaoshaService.miaosha(user,goods);

    }


   /* @RabbitListener(queues = MQConfig.QUEUE)
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
    }*/
}
