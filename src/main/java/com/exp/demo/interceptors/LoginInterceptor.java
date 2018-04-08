package com.exp.demo.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {
    public LoginInterceptor() {
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exc) throws Exception {
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        if(url.indexOf("/login.html") < 0 && url.indexOf("/login") < 0) {
            HttpSession session = request.getSession();
            String username = (String)session.getAttribute("username");
            if(username != null) {
                return true;
            } else {
                request.getRequestDispatcher("/login.html").forward(request, response);
                return false;
            }
        } else {
            return true;
        }
    }
}
