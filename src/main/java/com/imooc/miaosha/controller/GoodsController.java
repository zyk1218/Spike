package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;


    /**
     * 参数解释：
     *  由于token的位置不可以确定，所以为了兼容性考虑，需要设置两个参数，一个是从cookie中拿的，一个是从request中拿的
     */
/*    @RequestMapping("/to_list")
    public String list(HttpServletResponse response,Model model,
                       @CookieValue(value = MiaoshaUserService.COOKIE_NAME_TOKEN,required = false) String cookieToken,
                       @RequestParam(value = MiaoshaUserService.COOKIE_NAME_TOKEN,required = false) String paramToken,
                        MiaoshaUser user){
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(response,token);
        model.addAttribute("user",miaoshaUser);
        return "goods_list";
    }*/


    /**
     * 相较于书写格式冗余，每次添加新的方法都需要书写大量的代码，所以我们采用参数解析器来给方法直接注入相关参数，
     * 以此减少代码量，有利于代码的阅读与维护。
     * @param model
     * @param miaoshaUser
     * @return
     */
    @RequestMapping("/to_list")
    public String list(Model model,MiaoshaUser miaoshaUser){
        model.addAttribute("user",miaoshaUser);
        List<GoodsVo> goodsList = goodsService.getGoodsVoList();
        model.addAttribute("goodsList",goodsList);
        return "goods_list";
    }
    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, MiaoshaUser miaoshaUser, @PathVariable("goodsId")long goodsId){
        model.addAttribute("user",miaoshaUser);
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        model.addAttribute("goods",goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀的状态
        int miaoshaStatus = 0;

        //距离开始的剩余时间
        int remainSeconds = 0;

        //秒杀还未开始
        if(now<startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        }else if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else{//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        return "goods_detail";

    }

}
