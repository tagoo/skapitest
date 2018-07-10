package com.sunrun.common.task;

import com.sunrun.common.config.IamConfig;
import com.sunrun.security.Operate;
import com.sunrun.utils.IamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Component
@Order(value = 1)
public class SkapiApplicationRunner implements ApplicationRunner {
    @Resource
    private Operate operate;
    @Resource
    private IamConfig iamConfig;
    @Resource
    private RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(SkapiApplicationRunner.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info(String.format("Start connecting to the IAM server,host:%s,port:%d",iamConfig.getHost(),iamConfig.getPort()));
        IamUtil.getInstance().setDefaultProp(iamConfig,restTemplate);
        logger.info("Successfully connect to the IAM server");
        System.out.println(IamUtil.getInstance().getAccessToken());
    }
}
