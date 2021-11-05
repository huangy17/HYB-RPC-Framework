package com.huangyb.rpc.server;

import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMesage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author huangyb
 * @create 2021-11-06 1:21
 */

/*
* 真正的工作线程，当server接收到来自客户端的新连接将会创建此工作线程用于该socket的后续交互。
* */
public class ServerWorkerRunnableTask implements Runnable{

    private Socket socket;
    private Object service;

    public ServerWorkerRunnableTask(Socket socket, Object service) {
        this.service = service;
        this.socket = socket;
    }

    @Override
    public void run() {
        //根据传入的socket获取输出和输入流，这里（server端）的输出和输入和rpcClient中的输出和输入流是对应关系
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequestMessage rpcRequestMessage = (RpcRequestMessage) objectInputStream.readObject();
            Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequestMessage.getParameters());
            objectOutputStream.writeObject(new RpcResponseMesage<>(RpcResponseMesage.SUCCESS_CODE,returnObject));
            objectOutputStream.flush();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
