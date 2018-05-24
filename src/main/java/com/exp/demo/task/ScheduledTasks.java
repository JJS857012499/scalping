package com.exp.demo.task;

import com.exp.demo.action.ExpectedEarningsAction;
import com.exp.demo.action.OrderAction;
import com.exp.demo.action.v2.OrderAction2;
import com.exp.demo.vo.ShouYiVo;
import com.exp.demo.wx.TemplateData;
import com.exp.demo.wx.WX_TemplateMsgUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exp.demo.wx.WX_TemplateMsgUtil.packJsonmsg;

/**
 * Created by 江俊升 on 2018/4/8.
 */
@Component
public class ScheduledTasks {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private OrderAction orderAction;

    @Autowired
    private OrderAction2 orderAction2;

    @Autowired
    private ExpectedEarningsAction expectedEarningsAction;

    @Value("${sd_wx_tempid}")
    private String tempid;
    @Value("#{'${sd_wx_openid}'.split(',')}")
    private List<String> openIdList;


    // cron表达式 秒 分钟 小时 日 月 星期 年
    //每天23点48分执行发货收货
    @Scheduled(cron = "0 35 23 * * ?")
    public void scheduleDeliveryandFinish() throws InterruptedException {
        Logger.info("定时任务执行批量登录操作");
        String loginall = orderAction.loginall();
        Logger.info("执行结果：" + loginall);
        Thread.sleep(500);

        Logger.info("定时任务执行批量发货");
        String delivery = orderAction.delivery();
        Logger.info("执行结果：" + delivery);
        Thread.sleep(500);

        Logger.info("定时任务执行批量收货");
        String finish = orderAction.finish();
        Logger.info("执行结果：" + finish);
    }

    @Scheduled(cron = "0 50 8 * * ?")
    public void scheduleDelivery0() throws InterruptedException {
        scheduleDelivery();
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduleFinish0() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 45 11 * * ?")
    public void scheduleDelivery1() throws InterruptedException {
        scheduleDelivery();
    }

    @Scheduled(cron = "0 15 12 * * ?")
    public void scheduleFinish1() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 0 21 * * ?")
    public void scheduleDelivery2() throws InterruptedException {
        scheduleDelivery();
    }

    @Scheduled(cron = "0 30 21 * * ?")
    public void scheduleFinish2() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 0 22 * * ?")
    public void scheduleDelivery3() throws InterruptedException {
        scheduleDelivery();
    }

    @Scheduled(cron = "0 30 22 * * ?")
    public void scheduleFinish3() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void scheduleDelivery4() throws InterruptedException {
        scheduleDelivery();
    }

    @Scheduled(cron = "0 30 23 * * ?")
    public void scheduleFinish4() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 0 20 * * ?")
    public void scheduleDelivery5() throws InterruptedException {
        scheduleDelivery();
    }

    @Scheduled(cron = "0 30 20 * * ?")
    public void scheduleFinish5() throws InterruptedException {
        scheduleFinish();
    }


    public void scheduleDelivery() throws InterruptedException {
        Logger.info("定时任务执行批量登录操作");
        String loginall = orderAction.loginall();
        Logger.info("执行结果：" + loginall);
        Thread.sleep(500);

        Logger.info("定时任务执行批量发货");
        String delivery = orderAction.delivery();
        Logger.info("执行结果：" + delivery);
    }

    public void scheduleFinish() throws InterruptedException {
        Logger.info("定时任务执行批量收货");
        String finish = orderAction.finish();
        Logger.info("执行结果：" + finish);
    }

    //每天7点执行加购物车
    @Scheduled(cron = "0 0 6 * * ?")
    public void scheduleOrder() throws InterruptedException {
        Logger.info("定时任务执行批量登录操作");
        String loginall = orderAction.loginall();
        Logger.info("执行结果：" + loginall);
        Thread.sleep(500);

        Logger.info("定时任务执行清空购物车");
        String removeCar = orderAction.removeCar();
        Logger.info("执行结果：" + removeCar);

        Logger.info("定时任务执行批量加购物车");
        String order = orderAction2.order();
        Logger.info("执行结果：" + order);
    }

    //每天19点执行账户资金收集
    @Scheduled(cron = "0 50 18 * * ?")
    public void scheduleShouge() throws InterruptedException {
        Logger.info("定时任务执行批量登录操作");
        String loginall = orderAction.loginall();
        Logger.info("执行结果：" + loginall);
        Thread.sleep(500);

        Logger.info("定时任务执行资金收集");
        String shouge = expectedEarningsAction.shouge();
        Logger.info("执行结果：" + shouge);
    }


    //收益警告，防止忘记支付
    @Scheduled(cron = "0 50 20 * * ?")
    public void schedulShouyiWarn() {
        List<ShouYiVo> shouYiVoList = orderAction.shouyi();
        if (CollectionUtils.isEmpty(shouYiVoList) || "-1".equals(shouYiVoList.get(0).getResult())) {
            for (String openId : openIdList) {
                Logger.warn("发送微信警告消息");
                senMsg(openId, tempid);
            }
        }
    }

    private void senMsg(String openId, String regTempId) {
        Map<String, TemplateData> param = new HashMap<>();
        //调用发送微信消息给用户的接口
        WX_TemplateMsgUtil.sendWechatMsgToUser(openId, regTempId, "", "#000000", packJsonmsg(param));
    }


}
