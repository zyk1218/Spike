package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{
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

    @Autowired
    MQSender mqSender;

    /**
     * 当库存为0时便不允许后续请求访问redis。
     */
    private Map<Long,Boolean> localOverMap = new HashMap<Long, Boolean>();


    /**
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        if(goodsVoList == null) return;
        for(GoodsVo good : goodsVoList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+good.getId(),good.getStockCount());
            localOverMap.put(good.getId(),false);
        }
    }


    /**
     * orderId:成功
     * -1:秒杀失败
     * 0：排队中（还没处理）
     * @param model
     * @param miaoshaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> result(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", miaoshaUser);
        if (miaoshaUser == null) return Result.error(CodeMsg.USER_ERROR);
        long result = miaoshaService.getMiaoshaResult(miaoshaUser.getId(),goodsId);
        return Result.success(result);
    }


    /**
     * QPS 2692.5
     * 2000 * 5
     *
     * 加入消息队列后：
     * QPS 4703.7
     * 2000 * 5
     * @param model
     * @param miaoshaUser
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",miaoshaUser);
        if(miaoshaUser == null) return Result.error(CodeMsg.USER_ERROR);

        //预减库存
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否秒杀到了（因为需要保证一人仅秒杀一次）
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(),goodsId);
        if(order != null) return  Result.error(CodeMsg.REPEAT_MIAOSHA);
        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setGoodsId(goodsId);
        message.setUser(miaoshaUser);
        mqSender.sendMiaoshaMessage(message);
        Result<Integer> success = Result.success(0);
        return success;// 0 代表排队中




       /* //判断库存
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否秒杀到了（因为需要保证一人仅秒杀一次）
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(miaoshaUser.getId(),goodsId);
        if(order != null){
            model.addAttribute("errmsg",CodeMsg.REPEAT_MIAOSHA.getMsg());
            return  Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        /**
         *  上述已完成判断，至此已符合秒杀要求，以下是秒杀具体过程
         *  1、减库存
         *  2、下订单
         *  3、写入秒杀订单
         *  该三个步骤应处于一个事务之中

        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser,goods);
        return Result.success(orderInfo); */
    }



}
