package com.imooc.miaosha.redis;

public interface KeyPrefix {
    /**
     * 有效期
     *  0 代表永不过期
     * @return
     */
    int expireSeconds();

    /**
     * 前缀
     * @return
     */
    String getPrefix();
}
