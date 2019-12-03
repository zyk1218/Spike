package com.imooc.miaosha.access;

import com.imooc.miaosha.domain.MiaoshaUser;

public class UserContext {

    /**
     * ThreadLocal是与当前线程绑定的，多线程下安全的类。
     */
    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
    }

    public static MiaoshaUser getUser(){
        return userHolder.get();
    }
}
