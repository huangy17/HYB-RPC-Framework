package com.huangyb.rpc.test;

import com.huangyb.rpc.RpcClient;
import com.huangyb.rpc.api.HiObject;
import com.huangyb.rpc.api.HiService;
import com.huangyb.rpc.RpcClientProxy;
import com.huangyb.rpc.netty.client.NettyRpcClient;
import com.huangyb.rpc.socket.client.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangyb
 * @create 2021-11-06 4:11
 */

@Slf4j
public class TestClient {

    public void testSocketClient(){
        RpcClient rpcClient = new SocketRpcClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        //HiService hiServiceClass = null;
        HiService hiService = rpcClientProxy.getProxy(HiService.class);
        HiObject obj = new HiObject(66, "Echo message test");
        String result  = hiService.sayHello(obj);
        System.out.println(result);
    }

    public void testNettyClient(){
        RpcClient rpcClient = new NettyRpcClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);
        //HiService hiServiceClass = null;
        HiService hiService = rpcClientProxy.getProxy(HiService.class);
        HiObject obj = new HiObject(66, "Echo message test");
        String result  = hiService.sayHello(obj);
        System.out.println(result);
    }

    public static void main(String[] args) {
        TestClient testClient = new TestClient();

        //testClient.testSocketClient();

        testClient.testNettyClient();
    }

}
