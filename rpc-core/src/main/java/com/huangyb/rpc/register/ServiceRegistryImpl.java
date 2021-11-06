package com.huangyb.rpc.register;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangyb
 * @create 2021-11-06 20:13
 */
@Slf4j
public class ServiceRegistryImpl implements ServiceRegistry{

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();


    @Override
    public synchronized <T> void register(T service) {
        //获取service的所有实现接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        //获取之后遍历接口并将接口及service分别作为键值对存入map中
        for(Class<?> inter : interfaces){
            //默认是接口名作为service名，所以这里需要保证每个接口对应一个对象
            if(serviceMap.containsKey(inter.getCanonicalName())) {
                log.info("The interface:{} has already registered",inter.getCanonicalName());
                continue;
            }
            //把出现的接口名作为键，service实体对象作为值，存入map中
            serviceMap.put(inter.getCanonicalName(),service);
            log.info("Register the interface:{} with service:{}",inter.getCanonicalName(),service.getClass().getCanonicalName());
        }

    }

    @Override
    public synchronized Object getServiceByName(String serviceName) throws Exception {
        Object service = null;
        if(serviceMap.containsKey(serviceName)){
            service = serviceMap.get(serviceName);
        }else{
            throw new Exception("Service not found");
        }
        log.info("Find service:{}", service.getClass().getCanonicalName());
        return service;
    }



    @Override
    public String toString() {
        return "ServiceRegistryImpl{" +
                "serviceMap=" + serviceMap +
                '}';
    }
}
