//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.exp.demo.action;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@ConfigurationProperties(
        prefix = "sd.login"
)
public class LoginAction {
    private static final Logger log = LoggerFactory.getLogger(LoginAction.class);
    private Map<String, String> userMap;
    @Value("${private.key}")
    private String key;

    public LoginAction() {
    }

    @RequestMapping(
            value = {"/"},
            method = {RequestMethod.GET}
    )
    public String login() {
        return "login";
    }

    @RequestMapping({"login"})
    public String login(HttpServletRequest request, String username, String password) throws Exception {
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            HttpSession session = request.getSession();
            String user = (String)session.getAttribute("username");
            if(user != null) {
                return "relogin";
            } else if(password.equals(this.userMap.get(username))) {
                session.setAttribute("username", username);
                return "relogin";
            } else {
                return "redirect:login.html";
            }
        } else {
            return "redirect:login.html";
        }
    }

    @RequestMapping({"login.html"})
    public String index(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String user = (String)session.getAttribute("username");
        return user != null?"relogin":"login";
    }

    @RequestMapping(
            value = {"zuce"},
            method = {RequestMethod.GET}
    )
    public String zuce() {
        return "zuce";
    }

    @RequestMapping(
            value = {"relogin"},
            method = {RequestMethod.GET}
    )
    public String relogin() {
        return "relogin";
    }

    public Map<String, String> getUserMap() {
        return this.userMap;
    }

    public void setUserMap(Map<String, String> userMap) {
        this.userMap = userMap;
    }
}
