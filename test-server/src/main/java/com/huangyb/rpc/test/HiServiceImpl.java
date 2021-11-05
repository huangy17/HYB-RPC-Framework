package com.huangyb.rpc.test;

import com.huangyb.rpc.api.HiObject;
import com.huangyb.rpc.api.HiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author huangyb
 * @create 2021-11-05 16:35
 */
public class HiServiceImpl implements HiService {
    private static final Logger logger = LoggerFactory.getLogger(HiServiceImpl.class);
    @Override
    public String sayHello(HiObject obj) {
        logger.info("received content：{}", obj.getContent());
        return "Hello! The result id is:" + obj.getId();
    }
}
