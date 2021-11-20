package com.huangyb.rpc.register;

import com.huangyb.rpc.utils.CuratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangyb
 * @create 2021-11-06 20:13
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider{

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private static final CuratorFramework zkClient= CuratorUtil.getZkClient();

    public ServiceProviderImpl() {
        log.info("创建ServiceProvider。。。");
        if(!zkClient.isStarted())
            zkClient.start();

        //此代码无用，可删除（这段代码是为了通过创建EphemeralNode测试服务端是否成功联通zookeeper）
//        StringBuilder servicePath = new StringBuilder(CuratorUtil.ZK_REGISTER_ROOT_PATH).append("/").append("TestZKClass");
//        //服务子节点下注册子节点：服务地址
//        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",7777);
//        servicePath.append(inetSocketAddress.toString());
//        CuratorUtil.createEphemeralNode(zkClient, servicePath.toString());
//        log.info("Create node successfully on zookeeper，the node is:{}", servicePath);
    }

    /**
     * 向服务端注册服务并向zookeeper创建提供该服务的服务器ip:port
     * @param service 服务端需要注册的方法
     * @param host 提供服务的服务端ip地址（注册到zookeeper的ip）
     * @param port 提供服务的服务端port（注册到zookeeper的port）
     * */
    @Override
    public synchronized <T> void addServiceToProviderAndZk(T service, String host, int port) {
        //获取service的所有实现接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        //获取之后遍历接口并将接口及service分别作为键值对存入map中
        for(Class<?> inter : interfaces){
            //默认是接口名作为service名，所以这里需要保证每个接口对应一个对象
            if(serviceMap.containsKey(inter.getCanonicalName())) {
                log.info("The interface:{} has already registered to provider",inter.getCanonicalName());
                continue;
            }
            //把出现的接口名作为键，service实体对象作为值，存入map中
            serviceMap.put(inter.getCanonicalName(),service);
            log.info("Register the interface:{} with service:{}",inter.getCanonicalName(),service.getClass().getCanonicalName());


            /*
            *
            * */
            //注册到zookeeper
            //根节点下注册子节点：服务
            StringBuilder servicePath = new StringBuilder(CuratorUtil.ZK_REGISTER_ROOT_PATH).append("/").append(inter.getCanonicalName());
            //服务子节点下注册子节点：服务地址
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host,port);
            servicePath.append(inetSocketAddress.toString());
            CuratorUtil.createEphemeralNode(zkClient, servicePath.toString());
            log.info("Create node successfully on zookeeper，the node is:{}", servicePath);

        }


    }

    @Override
    public synchronized Object getServiceFromProvider(String serviceName) throws Exception {
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
    public InetSocketAddress lookupService(String serviceName) {
        String serviceAddress = CuratorUtil.getChildrenNodes(zkClient, serviceName).get(0);
        log.info("成功找到服务地址:{}", serviceAddress);
        return new InetSocketAddress(serviceAddress.split(":")[0], Integer.parseInt(serviceAddress.split(":")[1]));
    }



    @Override
    public String toString() {
        return "ServiceRegistryImpl{" +
                "serviceMap=" + serviceMap +
                '}';
    }
}
