package com.huangyb.rpc.netty.client;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.huangyb.rpc.ProcotolFrameDecoder;
import com.huangyb.rpc.RpcClient;
import com.huangyb.rpc.codec.CommonDecoder;
import com.huangyb.rpc.codec.CommonEncoder;
import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;
import com.huangyb.rpc.netty.server.NettyRpcServerHandler;
import com.huangyb.rpc.protocol.MessageCodec;
import com.huangyb.rpc.protocol.Serializer;
import com.huangyb.rpc.register.ServiceProvider;
import com.huangyb.rpc.register.ServiceProviderImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author huangyb
 * @create 2021-11-07 20:10
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    //private static Bootstrap bootstrap;
    private volatile static Channel channel;
    //private String ip;
    //private int port;
    ServiceProvider serviceProvider;


    public NettyRpcClient(){
        serviceProvider = new ServiceProviderImpl();
    }

    public static Channel getChannel(String ip, int port){
        if(channel==null) {
            synchronized (NettyRpcClient.class) {
                if (channel == null) {
                    NioEventLoopGroup group = new NioEventLoopGroup();
                    LoggingHandler LOG_HANDLER = new LoggingHandler(LogLevel.DEBUG);
                    MessageCodec MESSAGE_CODEC = new MessageCodec();
                    NettyRpcClientHandler nettyRpcClientHandler = new NettyRpcClientHandler();

                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.channel(NioSocketChannel.class)
                            .group(group)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0));
                                    //ch.pipeline().addLast(new ProcotolFrameDecoder());
//                                    ch.pipeline().addLast(LOG_HANDLER);
                                    ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
                                    ch.pipeline().addLast(MESSAGE_CODEC);
                                    ch.pipeline().addLast(nettyRpcClientHandler);
                                }
                            });
                    try {
                        channel = bootstrap.connect(ip, port).sync().channel();
                        channel.closeFuture().addListener(future -> {
                            group.shutdownGracefully();
                        });
                        return channel;
                        //return getChannel("127.0.0.1", 92222);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    return channel;
                    //return getChannel("127.0.0.1", 92222);
                }
            }
        }else{
            return channel;
            //return getChannel("127.0.0.1", 92222);
        }
        return channel;
        //return getChannel("127.0.0.1", 92222);
    }





    @Override
    public Object sendRpcRequestMessage(RpcRequestMessage rpcRequestMessage) {

        try {
            //Channel channel = bootstrap.connect(ip, port).sync().channel();
            InetSocketAddress inetSocketAddress =
                    serviceProvider.lookupService(rpcRequestMessage.getInterfaceName());
            Channel ch = getChannel(inetSocketAddress.getHostString(),inetSocketAddress.getPort());
            ch.writeAndFlush(rpcRequestMessage).addListener(future -> {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    log.error("error", cause);
                }else{
                    log.info(String.format("客户端发送消息: %s", rpcRequestMessage));
                }
            });
           // ch.closeFuture().sync();
            // 准备一个空 Promise 对象，来接收结果                  指定 promise 对象异步接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
            NettyRpcClientHandler.PROMISES.put(rpcRequestMessage.getId(), promise);


            // 4. 等待 promise 结果
            promise.await();
            RpcResponseMessage rpcResponseMessage = null;
            if(promise.isSuccess()) {
                // 调用正常
                rpcResponseMessage = ((RpcResponseMessage) promise.getNow());
                return rpcResponseMessage.getData();

            } else {
                // 调用失败
                throw new RuntimeException(promise.cause());
            }
        } catch (Exception e) {
            log.error("client error", e);
        } finally {

        }
        return null;
    }

    /*private String host;
    private int port;
    private static final Bootstrap bootstrap;

    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        NettyRpcServerHandler NETTY_RPCSERVER_HANDLER = new NettyRpcServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //ch.pipeline().addLast(new ProcotolFrameDecoder());
                        //ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
//                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(new CommonEncoder(Serializer.Algorithm.Json));
                        ch.pipeline().addLast(new CommonDecoder());

                        ch.pipeline().addLast(NETTY_RPCSERVER_HANDLER);
                    }
                });
    }

    @Override
    public Object sendRpcRequestMessage(RpcRequestMessage rpcRequest) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            log.info("客户端连接到服务器 {}:{}", host, port);
            Channel channel = future.channel();
            if(channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        log.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                    } else {
                        log.error("发送消息时有错误发生: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponseMessage> key = AttributeKey.valueOf("rpcResponse");
                RpcResponseMessage rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            log.error("发送消息时有错误发生: ", e);
        }
        return null;
    }*/

}
