package com.huangyb.rpc.netty.client;

import com.huangyb.rpc.message.RpcResponseMessage;
import com.sun.applet2.AppletParameters;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangyb
 * @create 2021-11-07 23:40
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage rpcResponseMessage) throws Exception {
        log.info("Client revived message: {} ", rpcResponseMessage);

        Promise<Object> promise = PROMISES.remove(rpcResponseMessage.getId());
        if (promise != null) {
            //Object returnValue = rpcResponseMessage;
            //Exception exceptionValue = msg.getExceptionValue();
            int responseCode = rpcResponseMessage.getCode();
            if(responseCode == RpcResponseMessage.FAIL_CODE) {
                promise.setFailure((Throwable) rpcResponseMessage.getData());
            } else if(responseCode == RpcResponseMessage.SUCCESS_CODE){
                promise.setSuccess(rpcResponseMessage);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("There is an error:");
        cause.printStackTrace();
        ctx.close();
    }



    /*private static final Logger logger = LoggerFactory.getLogger(NettyRpcClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        try {
            logger.info(String.format("客户端接收到消息: %s", msg));
            AttributeKey<RpcResponseMessage> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }*/

}
