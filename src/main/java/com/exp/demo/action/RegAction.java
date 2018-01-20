package com.exp.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.exp.demo.ST;
import com.exp.demo.utils.HttpClientUtil;
import com.exp.demo.utils.Md5Util;
import com.exp.demo.vo.RegisterReqVo;
import com.exp.demo.vo.loginVo;
import net.jhelp.mass.utils.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/12/29 下午1:28
 */
@Controller
public class RegAction {

    private static final Logger log = LoggerFactory.getLogger(RegAction.class);
    private static String P = "jsonData={phone:PHONECODE,code:111111}";

    @RequestMapping(value = "regs")
    public String regs(String accounts, String password){
        log.info("accounts : {}", accounts);
        if(StringKit.isNotBlank(accounts)){
            accounts = accounts.replaceAll("\r\n", "");
            String[] tmp = accounts.split(",");
            for(String s : tmp){
                reg(s, password);
                ST.phones.add(s);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return "index";
    }

    private void reg(String s, String password){
        String p = P.replace("PHONECODE", s);
        String result = HttpClientUtil.sendPost(ST.p_smsCode.concat(p), null);
        log.info(result);
        JSONObject jsonObject = JSON.parseObject(result);
        if("000".equalsIgnoreCase(jsonObject.getString("result"))){
            //注册
            RegisterReqVo vo = new RegisterReqVo();
            vo.setPhone(s);
            if(StringKit.isNotBlank(password)){
                vo.setPassword(Md5Util.encoderByMd5(password));
            }
            String s1 = JSON.toJSONString(vo);
            log.info(s1);
            result = HttpClientUtil.sendHttpsPost(ST.p_regist, s1, ST.headers);
            log.info("reg result : {}", result);
            //激活
            loginVo loginVo = new loginVo();
            if(StringKit.isNotBlank(password)){
                loginVo.setPassword(Md5Util.encoderByMd5(password));
            }
            loginVo.setPhone(s);
            result = HttpClientUtil.sendHttpsPost(ST.p_login, JSON.toJSONString(loginVo), ST.headers);
            log.info("login result : {}", result);
            //加入白名单
            result = HttpClientUtil.get(ST.p_whiltelist.concat(s), ST.headers);
            log.info("whiteList result : {}", result);
        }
    }

    @RequestMapping(value = "relogin", method = {RequestMethod.POST})
    public String login(String accounts, String password){
        log.info("accounts : {}", accounts);
        if(StringKit.isNotBlank(accounts)) {
            accounts = accounts.replaceAll("\r\n", "");
            String[] tmp = accounts.split(",");
            for (String s : tmp) {
                //登录
                loginVo loginVo = new loginVo();
                if(StringKit.isNotBlank(password)){
                    loginVo.setPassword(Md5Util.encoderByMd5(password));
                }
                loginVo.setPhone(s);
                String result = HttpClientUtil.sendHttpsPost(ST.p_login, JSON.toJSONString(loginVo), ST.headers);
                log.info("login result : {}", result);
                JSONObject jsonObject1 = JSON.parseObject(result);
                JSONObject data = jsonObject1.getJSONObject("data");
                log.info("sessionId : {}", data.getString("sessionId"));
                ST.cache.put(s, data.getString("sessionId"));
                ST.phones.add(s);
            }
        }

        return "index";
    }
}
