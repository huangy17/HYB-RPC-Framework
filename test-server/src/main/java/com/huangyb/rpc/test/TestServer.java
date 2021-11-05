package com.huangyb.rpc.test;

import com.huangyb.rpc.api.HiService;
import com.huangyb.rpc.server.RpcServer;

/**
 * @author huangyb
 * @create 2021-11-06 4:05
 */
public class TestServer {

    public static void main(String[] args) {
        //测试的方法
        HiService hiService = new HiServiceImpl();
        //初始化rpcServer以及连接线程池
        RpcServer rpcServer = new RpcServer();
        //创建socket绑定9000端口，传入测试的方法供rpcServer中的工作线程后续执行
        rpcServer.registry(9000, hiService);
    }


}
