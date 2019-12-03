package com.imooc.miaosha.config;

import com.imooc.miaosha.access.UserContext;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 这个类就是作为MiaoshaUser的参数解析器的
 */
@Service
public class UserArgumentResolvers implements HandlerMethodArgumentResolver{
    @Autowired
    MiaoshaUserService miaoshaUserService;


    /**
     * 这个方法的作用是判断我们所需要的参数类型是否匹配，如果匹配就进行第二个方法的操作。
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getUser();
    }

}
