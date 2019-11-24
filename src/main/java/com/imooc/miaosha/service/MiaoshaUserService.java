package com.imooc.miaosha.service;


import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id){
        //取缓存
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if(miaoshaUser != null) return miaoshaUser;
        //取数据库
        miaoshaUser = miaoshaUserDao.getById(id);
        if(miaoshaUser != null) redisService.set(MiaoshaUserKey.getById, "" + id, miaoshaUser);
        return miaoshaUser;
    }

    public boolean updatePassword(String token,long id,String passwordNew){
        //取user
        MiaoshaUser miaoshaUser = getById(id);
        if(miaoshaUser == null) throw  new GlobalException(CodeMsg.MOBILE_NOT_EXISTS);
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.fromPassToDBPass(passwordNew,miaoshaUser.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //更新缓存
        redisService.delete(MiaoshaUserKey.getById,""+id);
        miaoshaUser.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token,token,miaoshaUser);
        return true;
    }

    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String fromPass = loginVo.getPassword();

        //判断手机号
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if(user == null)    throw new GlobalException(CodeMsg.MOBILE_NOT_EXISTS);


        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String clcPass = MD5Util.fromPassToDBPass(fromPass,saltDB);
        if(!dbPass.equals(clcPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(user,token,response);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)) return null;
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        //延长cookie的有效期
        if(user != null){
            addCookie(user,token,response);
        }

        return user;
    }

    /**
     * 校验完毕，没有异常发生，此时生成cookie，用来构建分布式的session
     *  构建分布式session的思路是：成功登陆后给用户生成一个session id 用来标识给用户，把这个id写入cookie中
     *  把cookie传给客户端，服务端根据cookie中的信息进行操作。
     * 生成token之后存入redis之中
     * 将token生成cookie
     */
    private void addCookie(MiaoshaUser user,String token,HttpServletResponse response){
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
