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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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

    public BufferedImage createVerifyCode(MiaoshaUser miaoshaUser, long goodsId) {
        if(miaoshaUser == null || goodsId == 0) return  null;
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        //set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,width,height);
        //draw the border
        g.setColor(Color.black);
        g.drawRect(0,0,width-1,height-1);
        //random instance
        Random rdm = new Random();
        //make some confusion
        for(int i=0;i<50;i++){
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);//返回伪随机的，均匀分布 int值介于0（含）和指定值（不包括），从该随机数生成器的序列绘制。
            g.drawOval(x,y,0,0);
        }
        //生成随机验证码
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(verifyCode,8,24);
        g.dispose();
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode,miaoshaUser.getId()+","+goodsId,rnd);
        return image;
    }

    private int calc(String verifyCode) {
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Integer eval = (Integer)engine.eval(verifyCode);
            return eval;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+','-','*'};
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp =  "" + num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(MiaoshaUser miaoshaUser, long goodsId, int verifyCode) {
        if(miaoshaUser == null || goodsId <= 0) return false;
        Integer trueRes = redisService.get(MiaoshaKey.getMiaoshaVerifyCode,miaoshaUser.getId()+","+goodsId,Integer.class);
        if((trueRes == null || trueRes - verifyCode != 0)){
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode,miaoshaUser.getId()+","+goodsId);
        return true;
    }
}
