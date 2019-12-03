package com.imooc.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            MiaoshaUser user = getUser(request,response);
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null) return true;
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){
                if(user == null){
                    render(response,CodeMsg.USER_ERROR);
                    return false;
                }
                key += "_" +user.getId();
            }
            Integer count = redisService.get(AccessKey.withExpireAccess(seconds),key,Integer.class);
            if(count == null){
                redisService.set(AccessKey.withExpireAccess(seconds),key,1);
            }else if(count < 5){
                redisService.incr(AccessKey.withExpireAccess(seconds),key);
            }else {
                render(response,CodeMsg.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream os = response.getOutputStream();
        String s = JSON.toJSONString(cm);
        os.write(s.getBytes("UTF-8"));
        os.flush();
        os.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response){

        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request,MiaoshaUserService.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) return null;
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoshaUserService.getByToken(response,token);
    }
    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(cookieNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
