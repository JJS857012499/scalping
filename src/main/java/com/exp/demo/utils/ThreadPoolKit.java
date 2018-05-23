package com.exp.demo.utils;

import java.util.concurrent.*;

public class ThreadPoolKit
{
    private static final int THREAD_POOL = 100;
    private static ExecutorService fixedThreadPool;
    
    public static ExecutorService get() {
        if (ThreadPoolKit.fixedThreadPool == null) {
            ThreadPoolKit.fixedThreadPool = Executors.newFixedThreadPool(100);
        }
        return ThreadPoolKit.fixedThreadPool;
    }
    
    static {
        ThreadPoolKit.fixedThreadPool = Executors.newFixedThreadPool(100);
    }
}
