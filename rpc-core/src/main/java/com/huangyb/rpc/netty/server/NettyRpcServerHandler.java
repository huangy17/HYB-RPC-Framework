package com.huangyb.rpc.netty.server;

import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;
import com.huangyb.rpc.register.ServiceRegistry;
import com.huangyb.rpc.register.ServiceRegistryImpl;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author huangyb
 * @create 2021-11-07 23:30
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private static final ServiceRegistry serviceRegistry = new ServiceRegistryImpl();


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequestMessage rpcRequestMessage = (RpcRequestMessage) msg;
        try {
            log.info("Server received request: {}", rpcRequestMessage);
            String interfaceName = rpcRequestMessage.getInterfaceName();
            //根据请求报文获取service名称并动态调用方法==================================
            Object service = serviceRegistry.getServiceByName(interfaceName);
            Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequestMessage.getParameters());
            log.info("Service:{} call method:{} successfully",rpcRequestMessage.getInterfaceName(),rpcRequestMessage.getMethodName());

            ChannelFuture future = ctx.writeAndFlush(
                    new RpcResponseMessage<>(RpcResponseMessage.SUCCESS_CODE,returnObject,rpcRequestMessage.getId()));
            future.addListener(ChannelFutureListener.CLOSE);
        }catch (Exception e){
            e.printStackTrace();
            ChannelFuture future = ctx.writeAndFlush(
                    new RpcResponseMessage<>(RpcResponseMessage.FAIL_CODE,new Exception(e.getCause()),rpcRequestMessage.getId()));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(rpcRequestMessage);
        }


    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage rpcRequestMessage) throws Exception {
        try {
            log.info("Server received request: {}", rpcRequestMessage);
            String interfaceName = rpcRequestMessage.getInterfaceName();
            //根据请求报文获取service名称并动态调用方法==================================
            Object service = serviceRegistry.getServiceByName(rpcRequestMessage.getInterfaceName());
            Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequestMessage.getParameters());
            log.info("Service:{} call method:{} successfully",rpcRequestMessage.getInterfaceName(),rpcRequestMessage.getMethodName());

            ChannelFuture future = ctx.writeAndFlush(
                    new RpcResponseMessage<>(RpcResponseMessage.SUCCESS_CODE,returnObject,rpcRequestMessage.getId()));
            future.addListener(ChannelFutureListener.CLOSE);
        }catch (Exception e){
            ChannelFuture future = ctx.writeAndFlush(
                    new RpcResponseMessage<>(RpcResponseMessage.FAIL_CODE,new Exception(e.getMessage()),rpcRequestMessage.getId()));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(rpcRequestMessage);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("There is an error:");
        cause.printStackTrace();
        ctx.close();
    }




    /*private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerHandler.class);
    //private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        //requestHandler = new RequestHandler();
        serviceRegistry = new ServiceRegistryImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage rpcRequestMessage) throws Exception {
        try {
            logger.info("服务器接收到请求: {}", rpcRequestMessage);
            //根据请求报文获取service名称并动态调用方法==================================
            Object service = serviceRegistry.getServiceByName(rpcRequestMessage.getInterfaceName());
            Method method = service.getClass().getMethod(rpcRequestMessage.getMethodName(), rpcRequestMessage.getParamTypes());
            Object returnObject = method.invoke(service, rpcRequestMessage.getParameters());
            log.info("Service:{} call method:{} successfully",rpcRequestMessage.getInterfaceName(),rpcRequestMessage.getMethodName());
            ChannelFuture future = ctx.writeAndFlush(
                    new RpcResponseMessage<>(RpcResponseMessage.SUCCESS_CODE,returnObject,rpcRequestMessage.getId()));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequestMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }*/

}
