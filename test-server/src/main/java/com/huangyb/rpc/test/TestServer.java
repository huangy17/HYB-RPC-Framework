package com.huangyb.rpc.test;

import com.huangyb.rpc.RpcServer;
import com.huangyb.rpc.api.HiService;
import com.huangyb.rpc.netty.server.NettyRpcServer;
import com.huangyb.rpc.register.ServiceRegistry;
import com.huangyb.rpc.register.ServiceRegistryImpl;
//import com.huangyb.rpc.socket.server.RpcServer;
import com.huangyb.rpc.socket.server.SocketRpcServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangyb
 * @create 2021-11-06 4:05
 */

@Slf4j
public class TestServer {

    public void testSocketServer(){
        //测试的方法
        HiService hiService = new HiServiceImpl();


        //初始化注册器
        ServiceRegistry serviceRegistry = new ServiceRegistryImpl();
        //注册方法到注册器中
        serviceRegistry.register(hiService);


        //初始化rpcServer
        RpcServer rpcServer = new SocketRpcServer(serviceRegistry);
        //启动rpcServer,创建socket绑定9000端口
        rpcServer.start(9000);
    }

    public void testNettyServer(){
        //测试的方法
        HiService hiService = new HiServiceImpl();

        //初始化注册器
        ServiceRegistry serviceRegistry = new ServiceRegistryImpl();
        //注册方法到注册器中
        serviceRegistry.register(hiService);


        //初始化rpcServer
        RpcServer rpcServer = new NettyRpcServer();
        //启动rpcServer,创建socket绑定9000端口
        rpcServer.start(9000);
    }


    public static void main(String[] args) {
        TestServer testServer = new TestServer();

        //testServer.testSocketServer();

        testServer.testNettyServer();
    }


}
