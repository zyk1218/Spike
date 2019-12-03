package com.imooc.miaosha.config;

import com.imooc.miaosha.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    UserArgumentResolvers userArgumentResolvers;

    @Autowired
    AccessInterceptor accessInterceptor;

    /**
     * 这个方法的作用就是匹配Controller中相关类型的参数，如果有就设置。
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolvers);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
