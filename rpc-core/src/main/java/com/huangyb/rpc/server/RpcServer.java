package com.huangyb.rpc.server;

import sun.nio.ch.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author huangyb
 * @create 2021-11-06 0:01
 */

/*
* 创建socket，监听端口，创建线程池执行工作线程
* */
public class RpcServer {

    HandlerSocketServerPool pool =null;
    //ServerSocket serverSocket = null;

    /*
    * 创建工作线程池，一个socket连接建立开启一个工作线程用于接收该socket上的数据
    * */
    public RpcServer(){
        pool = new HandlerSocketServerPool(50, 100);
    }
    //
    public void registry(int port, Object service){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();
                //3. 吧socket对象交给线程池进行处理
                //吧socket封装成一个任务对象交给线程池处理
                pool.execute(new ServerWorkerRunnableTask(socket,service));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
