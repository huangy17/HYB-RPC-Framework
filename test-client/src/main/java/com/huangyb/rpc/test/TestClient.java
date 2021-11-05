package com.huangyb.rpc.test;

import com.huangyb.rpc.api.HiObject;
import com.huangyb.rpc.api.HiService;
import com.huangyb.rpc.client.RpcClientProxy;

/**
 * @author huangyb
 * @create 2021-11-06 4:11
 */
public class TestClient {


    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9000);
        //HiService hiServiceClass = null;
        HiService hiService = rpcClientProxy.getProxy(HiService.class);
        HiObject obj = new HiObject(66, "Echo message test");
        String result  = hiService.sayHello(obj);
        System.out.println(result);
    }

}
