package com.huangyb.rpc.register;

import java.net.InetSocketAddress;

/**
 * @author huangyb
 * @create 2021-11-06 20:13
 */
public interface ServiceProvider {

    <T> void addServiceToProviderAndZk(T service,String host, int port);
    Object getServiceFromProvider(String serviceName) throws Exception;
    InetSocketAddress lookupService(String serviceName);
}
