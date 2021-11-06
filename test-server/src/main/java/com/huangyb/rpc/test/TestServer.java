package com.huangyb.rpc.test;

import com.huangyb.rpc.api.HiService;
import com.huangyb.rpc.register.ServiceRegistry;
import com.huangyb.rpc.register.ServiceRegistryImpl;
import com.huangyb.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangyb
 * @create 2021-11-06 4:05
 */

@Slf4j
public class TestServer {

    public static void main(String[] args) {
        //测试的方法
        HiService hiService = new HiServiceImpl();


        //初始化注册器
        ServiceRegistry serviceRegistry = new ServiceRegistryImpl();
        //注册方法到注册器中
        serviceRegistry.register(hiService);


        //初始化rpcServer
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        //启动rpcServer,创建socket绑定9000端口
        rpcServer.start(9000);
    }


}
