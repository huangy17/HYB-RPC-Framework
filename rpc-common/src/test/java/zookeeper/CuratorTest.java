package zookeeper;

import com.huangyb.rpc.utils.CuratorUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangyb
 * @create 2021-11-15 16:52
 */
public class CuratorTest {

    @Test
    public void testConnection(){
        CuratorFramework curatorFramework = CuratorUtil.getZkClient();
        curatorFramework.start();
        CuratorUtil.createEphemeralNode(curatorFramework, "/hiService/127.0.0.1:9000");
    }


}
