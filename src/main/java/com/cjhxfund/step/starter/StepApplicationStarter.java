package com.cjhxfund.step.starter;

import org.cockburn.disruptor.MDFDisruptorWrapper;
import com.cjhxfund.common.disruptor.BaseDisruptorWrapper;
import com.cjhxfund.step.application.client.StepApplicationClient;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StepApplicationStarter {

    private static Logger logger = LoggerFactory.getLogger(StepApplicationStarter.class);

    public static void main(String args[]) {
        logger.info("Trading step testing service is starting");

        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        BaseDisruptorWrapper inputDisruptor = new MDFDisruptorWrapper(new EventHandler[]{});

        StepApplicationClient client = (StepApplicationClient) ctx.getBean("stepApplication");
        client.setRingBuffer(inputDisruptor);

        logger.info("Trading step application started");
    }
}
