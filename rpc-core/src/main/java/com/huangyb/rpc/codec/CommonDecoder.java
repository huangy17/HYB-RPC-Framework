package com.huangyb.rpc.codec;

import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;
import com.huangyb.rpc.protocol.Serializer;
import com.huangyb.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * 通用的解码拦截器
 * @author ziyang
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private static final int REQUEST = 0;
    private static final int RESPONSE = 1;
    private static final Serializer.Algorithm serializeAigorithm = Serializer.Algorithm.Json;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        //byte version = in.readByte();
        byte serializerAlgorithmCode = in.readByte(); // 0 或 1
        byte messageType = in.readByte(); // Response or Request: 0,1...
        int sequenceId = in.readInt();
        //in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 找到反序列化算法
        //Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithmCode];

        // 确定具体消息类型
        CommonSerializer serializer = CommonSerializer.getByCode(1);
        Class<?> messageClass = (messageType==REQUEST) ? RpcRequestMessage.class : RpcResponseMessage.class;
        Object message = serializer.deserialize(bytes,messageClass);
//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.debug("{}", message);
        out.add(message);
    }

}
