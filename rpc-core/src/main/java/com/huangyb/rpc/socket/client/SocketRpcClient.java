package com.huangyb.rpc.socket.client;

import com.huangyb.rpc.RpcClient;
import com.huangyb.rpc.message.RpcRequestMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author huangyb
 * @create 2021-11-05 23:08
 */

/*
* 建立socket与客户端socket连接。
* 传递客户端的请求及结果。
*
* */
public class SocketRpcClient implements RpcClient {

    private String ip;
    private int port;

    public SocketRpcClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    /*
     * 1.创建socket与服务端相同端口的socket进行连接
     * 2. 将封装好的RpcRequestMessage发送到server的socket进行方法调用，
     * 3. 将结果输入流（服务端的输出流对应于客户端的输入流）中的对象返回。
     * */
    public Object sendRpcRequestMessage(RpcRequestMessage rpcRequestMessage) {
        //1.创建socket与server的socket连接
        Socket socket = null;
        try{
            socket = new Socket(ip,port);
            //2. 根据socket创建输出和输入流（这里的输出流对应于服务端的输出流，这里的输入流对应于服务端的输入流）
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //3. 将rpcRequestMessage通过输出流发送到rpcServer
            objectOutputStream.writeObject(rpcRequestMessage);
            objectOutputStream.flush();
            //4. server端返回结果到这里的objectInputStream
            return objectInputStream.readObject();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            //if(socket!=null) socket

        }
    }
}
