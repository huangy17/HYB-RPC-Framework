package com.huangyb.rpc;

import com.huangyb.rpc.RpcClient;
import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;
import com.huangyb.rpc.socket.client.SocketRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author huangyb
 * @create 2021-11-05 22:25
 */
public class RpcClientProxy implements InvocationHandler {

    private RpcClient rpcClient;
    private String ip;
    private int port;

    //public RpcClientProxy(){};

/*    public RpcClientProxy(String ip, int port){
        this.ip = ip;
        this.port = port;
    }*/

    public RpcClientProxy(RpcClient rpcClient){
        this.rpcClient = rpcClient;
    }

    public void setRpcClient(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public RpcClient getRpcClient() {
        return rpcClient;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /*
    * 获取代理对象供客户端测试使用
    * */
    public <T> T getProxy(Class<T> target){
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //根据客户端调用的方法封装rpcRequestMessage对象
        RpcRequestMessage rpcRequestMessage = new RpcRequestMessage();
        rpcRequestMessage.setId(1);
        rpcRequestMessage.setInterfaceName(method.getDeclaringClass().getName());
        rpcRequestMessage.setMethodName(method.getName());
        rpcRequestMessage.setParameters(args);
        rpcRequestMessage.setParamTypes(method.getParameterTypes());

        //RpcClient rpcClient = new SocketRpcClient();
//        RpcResponseMessage rpcResponseMessage = (RpcResponseMessage) rpcClient.sendRpcRequestMessage(rpcRequestMessage);
//        Integer code = rpcResponseMessage.getCode();
//        System.out.println(code);
//        return rpcResponseMessage.getData();

        return rpcClient.sendRpcRequestMessage(rpcRequestMessage);
    }
}
