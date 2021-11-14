package com.huangyb.rpc.socket.server;

import com.huangyb.rpc.RpcServer;
import com.huangyb.rpc.register.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class SocketRpcServer implements RpcServer {

    private final HandlerSocketServerPool pool;
    //ServerSocket serverSocket = null;
    private final ServiceRegistry serviceRegistry;
    /*
    * 创建工作线程池，一个socket连接建立开启一个工作线程用于接收该socket上的数据
    * */
    public SocketRpcServer(ServiceRegistry serviceRegistry){
        log.info("Initial RpcServer...");
        //log.info("Initial threadPool...");
        pool = new HandlerSocketServerPool(50, 100);
        log.info("Initial serviceRegistry...");
        this.serviceRegistry = serviceRegistry;
    }
    //
//    public void registry(int port, Object service){
//
//    }

    public void start(int port){
        log.info("start rpcServer...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while(true) {
                log.info("Waiting for connection...");
                Socket socket = serverSocket.accept();
                log.info("Client connecting...: {}:{}",socket.getInetAddress(),socket.getPort());
                //3. 吧socket对象交给线程池进行处理
                //吧socket封装成一个任务对象交给线程池处理
                pool.execute(new ServerWorkerRunnableTask(socket,serviceRegistry));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
