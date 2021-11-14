package com.huangyb.rpc.netty.server;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.huangyb.rpc.ProcotolFrameDecoder;
import com.huangyb.rpc.RpcServer;
import com.huangyb.rpc.codec.CommonDecoder;
import com.huangyb.rpc.codec.CommonEncoder;
import com.huangyb.rpc.protocol.MessageCodec;
import com.huangyb.rpc.protocol.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangyb
 * @create 2021-11-07 20:09
 */
@Slf4j
public class NettyRpcServer implements RpcServer {



    @Override
    public void start(int port) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        NettyRpcServerHandler NETTY_RPCSERVER_HANDLER = new NettyRpcServerHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .channel(NioServerSocketChannel.class)
                    .group(boss, worker).option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0));
                            //ch.pipeline().addLast(new ProcotolFrameDecoder());
                            //ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(NETTY_RPCSERVER_HANDLER);
                        }
                    });
//            Channel channel = serverBootstrap.bind(port).sync().channel();
//            channel.closeFuture().sync();
            ChannelFuture future = serverBootstrap.bind(port).sync();
            log.info("Start Server successfully: port:{}",port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    /*@Override
    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        MessageCodec MESSAGE_CODEC = new MessageCodec();
        NettyRpcServerHandler NETTY_RPCSERVER_HANDLER = new NettyRpcServerHandler();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //ch.pipeline().addLast(new ProcotolFrameDecoder());
                            //ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.INFO));
                            //ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(new CommonEncoder(Serializer.Algorithm.Json));
                            ch.pipeline().addLast(new CommonDecoder());
                            ch.pipeline().addLast(NETTY_RPCSERVER_HANDLER);
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }*/
}
