package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;
    
    @Autowired
    RedisService redisService;


    /**
     * 事务处理三个操作：减库存，下订单，写入秒杀订单
     *
     * @param miaoshaUser
     * @param goods
     * @return
     */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            //写订单
            return orderService.createOrder(miaoshaUser, goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }

    }

    public long getMiaoshaResult(Long id, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(id,goodsId);
        if(order != null) return order.getOrderId();
        else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;
            } else{
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }

    public void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);
    }


    public boolean checkPath(MiaoshaUser miaoshaUser, long goodsId, String path) {
        if(miaoshaUser == null || path == null) return  false;
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath,""+miaoshaUser.getId()+"_"+goodsId,String.class);
        return path.equals(pathOld);
    }

    public String createMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,""+miaoshaUser.getId()+"_"+goodsId,str);
        return  str;
    }
}
