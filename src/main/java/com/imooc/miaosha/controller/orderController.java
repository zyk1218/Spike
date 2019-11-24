package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class orderController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser miaoshaUser, @RequestParam("orderId")long orderId){
        if(miaoshaUser == null) return Result.error(CodeMsg.USER_ERROR);
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) return Result.error(CodeMsg.ORDER_NOT_EXIST);
        long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoodsVo(goodsVo);
        vo.setOrderInfo(order);
        return Result.success(vo);
    }



}
