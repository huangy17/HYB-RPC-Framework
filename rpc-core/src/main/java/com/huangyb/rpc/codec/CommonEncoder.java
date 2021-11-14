package com.huangyb.rpc.codec;

import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;
import com.huangyb.rpc.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * 通用的编码拦截器
 * @author ziyang
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private static final int REQUEST = 0;
    private static final int RESPONSE = 1;
    private static final Serializer.Algorithm serializeAigorithm = Serializer.Algorithm.Json;
    private final Serializer.Algorithm serializer;

    public CommonEncoder(Serializer.Algorithm serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});

        // 2. 1 字节的序列化方式 jdk 0 , json 1
        //out.writeByte(Config.getSerializerAlgorithm().ordinal());
        out.writeByte(serializeAigorithm.ordinal());
        // 3. 4 字节的指令类型
        out.writeInt(msg instanceof RpcRequestMessage ? REQUEST : RESPONSE);
        // 4. 4 个字节的请求报文的，promise需要使用id查找结果
        out.writeInt(msg instanceof RpcRequestMessage? ((RpcRequestMessage) msg).getId():((RpcResponseMessage) msg).getId());
//        // 无意义，对齐填充
//        out.writeByte(0xff);
        //  获取内容的字节数组
        byte[] bytes = serializer.serialize(msg);
        // 5. 长度
        out.writeInt(bytes.length);
        // 6. 写入内容
        out.writeBytes(bytes);
        //debugAll(bytes);
        //outList.add(out);
    }

}
