package com.exp.demo;

import com.exp.demo.ex.MyExceptionHandler;
import com.exp.demo.in.WebInterceptor;
import com.exp.demo.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/11/5 下午4:18
 */
public class WebMvcConfigration extends WebMvcConfigurerAdapter {

    //@Autowired
    //private MyConfig myConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        // addPathPatterns 用于添加拦截规则
        // excludePathPatterns 用户排除拦截
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/wechat/**","/websocket/**");
        registry.addInterceptor(new WebInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new MyExceptionHandler());
        super.configureHandlerExceptionResolvers(exceptionResolvers);
    }

}
