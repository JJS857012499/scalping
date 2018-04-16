package com.exp.demo.wx;

import java.util.HashMap;
import java.util.Map;

import static com.exp.demo.wx.WX_TemplateMsgUtil.packJsonmsg;

/**
 * Created by 江俊升 on 2018/4/16.
 */
public class Test {

    public static void main(String[] args) {
        //推送微信消息
        String regTempId = "mhtd6mAN9ou-SOwX9q_Sy8XqJfCcTABqOqSuTi_6_gw";
        senMsg("ojNZ6uFHAWd7VbHe48cqkyPvbAVU", regTempId); //深水暗流
        senMsg("ojNZ6uCni-WUXrLg0qY67xRr5GjU", regTempId); //Rainbow
    }

    static void senMsg(String openId, String regTempId) {
        //用户是否订阅该公众号标识 (0代表此用户没有关注该公众号 1表示关注了该公众号)
        Integer state = WX_UserUtil.subscribeState(openId);
        // 绑定了微信并且关注了服务号的用户 , 注册成功-推送注册短信
        if (state == 1) {
            Map<String, TemplateData> param = new HashMap<>();
            //调用发送微信消息给用户的接口
            WX_TemplateMsgUtil.sendWechatMsgToUser(openId, regTempId, "", "#000000", packJsonmsg(param));
        }
    }
}