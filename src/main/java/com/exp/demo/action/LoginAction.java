package com.exp.demo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/12/29 下午12:41
 */
@Controller
public class LoginAction {
    private static final Logger log = LoggerFactory.getLogger(LoginAction.class);

    @Value("${private.key}")
    private String key;

    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String login(){
        return "login";
    }

    @RequestMapping(value = "index")
    public String index(String loginName, String password){
        log.info("loginName:{}, password:{}", loginName, password);
        if(!key.equalsIgnoreCase(password)){
            throw new RuntimeException("口令不正确");
        }
        return "index";
    }

    @RequestMapping(value = "zuce", method = {RequestMethod.GET})
    public String zuce(){
        return "zuce";
    }

    @RequestMapping(value = "relogin", method = {RequestMethod.GET})
    public String relogin(){
        return "relogin";
    }
}
