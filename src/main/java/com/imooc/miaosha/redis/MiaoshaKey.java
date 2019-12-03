package com.imooc.miaosha.redis;

public class MiaoshaKey extends BasePrefix {

    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }
    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0,"goodsOver");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60,"miaoshaPath");
    public static KeyPrefix getMiaoshaVerifyCode = new MiaoshaKey(300,"verifyCode");
}
