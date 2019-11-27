package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo>  getGoodsVoList(){
        return goodsDao.getGoodsVoList();
    }

    public GoodsVo getGoodsVoById(long goodsId) {
        return goodsDao.getGoodsVoById(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
//        MiaoshaGoods g = new MiaoshaGoods();
//        g.setId(goods.getId());
        int ret = goodsDao.reduceStock(goods.getId());
        return ret > 0;
    }
}
