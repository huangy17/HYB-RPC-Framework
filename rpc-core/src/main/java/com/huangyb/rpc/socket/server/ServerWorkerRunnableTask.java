package com.huangyb.rpc.socket.server;

import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;
import com.huangyb.rpc.register.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author huangyb
 * @create 2021-11-06 1:21
 */

/*
 * 真正的工作线程，当server接收到来自客户端的新连接将会创建此工作线程用于该socket的后续交互。
 * */
@Slf4j
public class ServerWorkerRunnableTask implements Runnable{

    private Socket socket;
    private ServiceRegistry serviceRegistry;

    public ServerWorkerRunnableTask(Socket socket, ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        this.socket = socket;
    }

    @Override
    public void run() {
        //根据传入的socket获取输出和输入流，这里（server端）的输出和输入和rpcClient中的输出和输入流是对应关系
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequestMessage rpcRequestMessage = (RpcRequestMessage) objectInputStream.readObject();
            //根据请求报文获取service名称并动态调用方法==================================
            Object service = serviceRegistry.getServiceByName(rpcRequestMessage.getInterfaceName());
            Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequestMessage.getParameters());
            log.info("Service:{} call method:{} successfully",rpcRequestMessage.getInterfaceName(),rpcRequestMessage.getMethodName());
            //将结果写入输出流=======================================================
            objectOutputStream.writeObject(new RpcResponseMessage<>(RpcResponseMessage.SUCCESS_CODE,returnObject,1));
            objectOutputStream.flush();
        } catch (Exception e){
            log.error("Service fail to call method:", e );
            //new RpcResponseMesage<>(RpcResponseMesage.FAIL_CODE,null);
        }
    }
}
