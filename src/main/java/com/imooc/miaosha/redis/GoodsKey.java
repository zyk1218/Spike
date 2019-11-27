package com.imooc.miaosha.redis;

public class GoodsKey extends BasePrefix{

    public GoodsKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }
    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getDetail = new GoodsKey(60,"de");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0,"msgt");

}
