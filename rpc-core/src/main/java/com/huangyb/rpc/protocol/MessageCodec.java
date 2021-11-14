package com.huangyb.rpc.protocol;

import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @author huangyb
 * @create 2021-11-08 0:54
 */


/*
*
* +---------------+------------------+--------------+-------------+-------------+
  |  Magic Number |  Serializer Type | Package Type | Message ID  | Data Length |
  |    4 bytes    |      1 bytes     |   4 bytes    |   4 bytes   |   4 bytes   |
  +---------------+------------------+--------------+-------------+-------------+
  |                                 Data Bytes                                  |
  |                          Length: ${Data Length}                             |
  +-----------------------------------------------------------------------------+
  ————————————————
*
* */
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Object> {

    private static final int REQUEST = 0;
    private static final int RESPONSE = 1;
    private static final Serializer.Algorithm serializeAigorithm = Serializer.Algorithm.Json;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});

        // 2. 1 字节的序列化方式 jdk 0 , json 1
        //out.writeByte(Config.getSerializerAlgorithm().ordinal());
        out.writeByte(serializeAigorithm.ordinal());
        // 3. 4 字节的指令类型
        out.writeInt(msg instanceof RpcRequestMessage? REQUEST : RESPONSE);
        // 4. 4 个字节的请求报文的，promise需要使用id查找结果
        out.writeInt(msg instanceof RpcRequestMessage? ((RpcRequestMessage) msg).getId():((RpcResponseMessage) msg).getId());
//        // 无意义，对齐填充
//        out.writeByte(0xff);
        //  获取内容的字节数组
        byte[] bytes = serializeAigorithm.serialize(msg);
        // 5. 长度
        out.writeInt(bytes.length);
        // 6. 写入内容
        out.writeBytes(bytes);
        //debugAll(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outList) throws Exception {
        int magicNum = in.readInt();
        //byte version = in.readByte();
        byte serializerAlgorithmCode = in.readByte(); // 0 或 1
        int messageType = in.readInt(); // Response or Request: 0,1...
        int sequenceId = in.readInt();
        //in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 找到反序列化算法
        //Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithmCode];

        // 确定具体消息类型
        Class<?> messageClass = (messageType==REQUEST) ? RpcRequestMessage.class : RpcResponseMessage.class;
        Object message = serializeAigorithm.deserialize(messageClass, bytes);
//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.debug("{}", message);
        outList.add(message);
    }
}
