package com.exp.demo.action;

import com.exp.demo.utils.SHA1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @author JianChow
 * @date 2018-03-31
 */
@Controller
@RequestMapping("/wechat")
public class Wechat {
    private static final Logger log = LoggerFactory.getLogger(Wechat.class);
    // 自定义 token
    private String TOKEN = "asd123456";

    @ResponseBody
    @RequestMapping(value = "token", method = RequestMethod.GET)
    public void toktn(HttpServletRequest request, HttpServletResponse response){
        PrintWriter print;
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");

        String[] str = { TOKEN, timestamp, nonce };
        Arrays.sort(str); // 字典序排序
        String bigStr = str[0] + str[1] + str[2];
        // SHA1加密
        String digest = new SHA1().getDigestOfString(bigStr.getBytes()).toLowerCase();
        // 确认请求来至微信
        log.info("signature:{},digest:{}",signature,digest);
        if (digest.equals(signature)) {
            try {
                print = response.getWriter();
                print.write(echostr);
                print.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
