package com.exp.demo.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author JianChow
 * @date 2018-04-02
 */
public  class Queue {
        //发货队列
       static ConcurrentLinkedQueue<String> fahuo = new ConcurrentLinkedQueue<String>();
        //收货队列
        static ConcurrentLinkedQueue<String> shouhuo = new ConcurrentLinkedQueue<String>();

        // 发货队列中加入账户
        public static void fahuoPut(String account){
            try {
                fahuo.add(account);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 发货队列中取走账户
        public static String fahuoGet() {
            String account=null;
            try {
                 account = fahuo.poll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return account;
        }

        // 发货队列中加入账户
        public static void shouHuoPut(String account){
            try {
                shouhuo.add(account);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 收货队列中取走账户
        public static String shouhuoGet(){
            String account = null;
            try {
                account = shouhuo.poll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return account;
        }


    public static void main(String[] args) {
        Queue.fahuoPut("18302319069");
        Queue.fahuoPut("18302319060");
        for (int i=0;i<3;i++){
            String s = Queue.fahuoGet();
            System.out.println("执行发货:"+s);
            String s1 = Queue.shouhuoGet();
            System.out.println("执行收货:"+s1);
        }

    }
}
