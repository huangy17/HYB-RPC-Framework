package com.huangyb.rpc.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author huangyb
 * @create 2021-11-06 1:13
 */
public class HandlerSocketServerPool {

    //1. 创建线程池成员变量
    private ExecutorService executorService;

    //2. 创建这个类的对象的时候需要初始化线程池
    public HandlerSocketServerPool(int maxThreadNum, int queueSize){
        executorService = new ThreadPoolExecutor(5, maxThreadNum, 120,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    //3. 提供方法来提交任务给线程池的任务队列来暂存，等线程池来处理
    public void execute(Runnable task){
        executorService.execute(task);
    }

}
