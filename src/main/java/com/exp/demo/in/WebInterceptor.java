package com.exp.demo.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/11/5 下午4:20
 */
public class WebInterceptor extends HandlerInterceptorAdapter implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object o) throws Exception {
        String uri = request.getRequestURI();

        //处理访问地址不存在的情况
        if(DispatcherType.ERROR == request.getDispatcherType()){
            throw new RuntimeException("访问的资源不存在或者无权限");
        }

        return true;
    }

}
