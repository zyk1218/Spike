package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
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


    @RequestMapping(value = "/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> miaosha(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",miaoshaUser);
        if(miaoshaUser == null) return Result.error(CodeMsg.USER_ERROR);

        //判断库存
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
         */
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser,goods);
        return Result.success(orderInfo);
    }


}
