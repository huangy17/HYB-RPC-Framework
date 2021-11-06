package com.huangyb.rpc.register;

/**
 * @author huangyb
 * @create 2021-11-06 20:13
 */
public interface ServiceRegistry {

    <T> void register(T service);
    Object getServiceByName(String serviceName) throws Exception;
}
