package com.exp.demo.task;

import com.exp.demo.action.ExpectedEarningsAction;
import com.exp.demo.action.OrderAction;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by 江俊升 on 2018/4/8.
 */
@Component
public class ScheduledTasks {

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private OrderAction orderAction;

    @Autowired
    private ExpectedEarningsAction expectedEarningsAction;

    // cron表达式 秒 分钟 小时 日 月 星期 年
    //每天23点48分执行发货收货
    @Scheduled(cron = "0 45 23 * * ?")
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

    @Scheduled(cron = "0 0 9 * * ?")
    public void scheduleDelivery0() throws InterruptedException {
        scheduleDelivery();
    }
    @Scheduled(cron = "0 10 9 * * ?")
    public void scheduleFinish0() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 55 11 * * ?")
    public void scheduleDelivery1() throws InterruptedException {
        scheduleDelivery();
    }
    @Scheduled(cron = "0 25 12 * * ?")
    public void scheduleFinish1() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 10 21 * * ?")
    public void scheduleDelivery2() throws InterruptedException {
        scheduleDelivery();
    }
    @Scheduled(cron = "0 40 21 * * ?")
    public void scheduleFinish2() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 10 22 * * ?")
    public void scheduleDelivery3() throws InterruptedException {
        scheduleDelivery();
    }
    @Scheduled(cron = "0 40 22 * * ?")
    public void scheduleFinish3() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 10 23 * * ?")
    public void scheduleDelivery4() throws InterruptedException {
        scheduleDelivery();
    }
    @Scheduled(cron = "0 40 23 * * ?")
    public void scheduleFinish4() throws InterruptedException {
        scheduleFinish();
    }

    @Scheduled(cron = "0 10 20 * * ?")
    public void scheduleDelivery5() throws InterruptedException {
        scheduleDelivery();
    }
    @Scheduled(cron = "0 40 20 * * ?")
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
    @Scheduled(cron = "0 0 7 * * ?")
    public void scheduleOrder() throws InterruptedException {
        Logger.info("定时任务执行批量登录操作");
        String loginall = orderAction.loginall();
        Logger.info("执行结果：" + loginall);
        Thread.sleep(500);

        Logger.info("定时任务执行批量加购物车");
        String order = orderAction.order();
        Logger.info("执行结果：" + order);
    }

    //每天19点执行账户资金收集
    @Scheduled(cron = "0 0 19 * * ?")
    public void scheduleShouge() throws InterruptedException {
        Logger.info("定时任务执行批量登录操作");
        String loginall = orderAction.loginall();
        Logger.info("执行结果：" + loginall);
        Thread.sleep(500);

        Logger.info("定时任务执行资金收集");
        String shouge = expectedEarningsAction.shouge();
        Logger.info("执行结果：" + shouge);
    }




}
