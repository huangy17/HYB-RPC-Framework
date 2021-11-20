package com.huangyb.rpc;

/**
 * @author huangyb
 * @create 2021-11-07 17:35
 */
public interface RpcServer {

    void start();

    <T> void registryServiceToProviderAndZk(T service);
}
