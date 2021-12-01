package com.huangyb.rpc.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huangyb.rpc.message.RpcRequestMessage;
import com.huangyb.rpc.message.RpcResponseMessage;


import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 用于扩展序列化、反序列化算法
 */
public interface Serializer {

    // 反序列化方法
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    // 序列化方法
    <T> byte[] serialize(T object);







    enum Algorithm implements Serializer {

        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败", e);
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败", e);
                }
            }
        },

        Json {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
//                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
//                String json = new String(bytes, StandardCharsets.UTF_8);
//                return gson.fromJson(json, clazz);
                try {
                    String jsonText = new String(bytes, StandardCharsets.UTF_8);
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object obj = objectMapper.readValue(jsonText, clazz);
                    if(obj instanceof RpcRequestMessage){
                        RpcRequestMessage rpcRequestMessage = (RpcRequestMessage) obj;
                        for(int i = 0; i < rpcRequestMessage.getParamTypes().length; i ++) {
                            Class<?> clazz1 = rpcRequestMessage.getParamTypes()[i];
                            if(!clazz.isAssignableFrom(rpcRequestMessage.getParameters()[i].getClass())) {
                                byte[] bytes1 = objectMapper.writeValueAsBytes(rpcRequestMessage.getParameters()[i]);
                                rpcRequestMessage.getParameters()[i] = objectMapper.readValue(bytes1, clazz1);
                            }
                        }
                        obj = rpcRequestMessage;
                    }else
                    if(obj instanceof RpcResponseMessage){
                        RpcResponseMessage rpcResponseMessage = (RpcResponseMessage) obj;

                        Class<?> clazz1 = rpcResponseMessage.getDataType();
                        if(!clazz.isAssignableFrom(rpcResponseMessage.getData().getClass())) {
                            byte[] bytes1 = objectMapper.writeValueAsBytes(rpcResponseMessage.getData());
                            rpcResponseMessage.setData(objectMapper.readValue(bytes1, clazz1));
                        }
                        obj = rpcResponseMessage;
                    }
                    return (T) obj;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public <T> byte[] serialize(T object) {
//                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
//                String json = gson.toJson(object);
//                return json.getBytes(StandardCharsets.UTF_8);
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.writeValueAsBytes(object);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }
    /*class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override             //   String.class
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }*/
}