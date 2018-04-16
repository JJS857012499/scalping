package com.exp.demo.wx;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by 江俊升 on 2018/4/16.
 */
@Component
public class WX_TokenUtil {

    private static Logger log = LoggerFactory.getLogger(WX_TokenUtil.class);


    private static String appId = "wx405b1946f6a269e0";
    private static String appSecret = "d915206eeb11aa6b5c53634aaf6ac31e";

    @Value("${sd_wx_appid}")
    public void setAppId(String appId) {
        WX_TokenUtil.appId = appId;
    }
    @Value("${sd_wx_appsecret}")
    public void setAppSecret(String appSecret) {
        WX_TokenUtil.appSecret = appSecret;
    }

    /**
     * 获得微信 AccessToken
     * access_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token。
     * 开发者需要access_token的有效期目前为2个小时，需定时刷新，重复获取将导致上次获取
     * 的access_token失效。
     */
//    @Cacheable
    public static AccessToken getWXToken() {
        AccessToken access_token = null;
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        JSONObject jsonObject = WX_HttpsUtil.httpsRequest(tokenUrl, "GET", null);
        if (null != jsonObject) {
            try {
                access_token = new AccessToken();
                access_token.setAccessToken(jsonObject.getString("access_token"));
                access_token.setExpiresin(jsonObject.getInteger("expires_in"));
            } catch (JSONException e) {
                access_token = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInteger("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return access_token;
    }
}
