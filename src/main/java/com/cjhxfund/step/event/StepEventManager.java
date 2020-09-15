package com.cjhxfund.step.event;

import com.cjhxfund.common.event.ConnectToMKTGwyFailure;
import com.cjhxfund.common.event.generator.MDFEventGenerator;
import com.cjhxfund.jms.connect.GeneralMQConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepEventManager {
    private static Logger logger = LoggerFactory.getLogger(StepEventManager.class);

    private static final String TRADING_MDF_ID = "5001";
    private static final String TRADING_MDF_NAME = "Trading-STEP";

    private GeneralMQConnector eventProducer;

    public void setEventProducer(GeneralMQConnector eventProducer) {
        this.eventProducer = eventProducer;
    }

    /**
     * send event of fail to connect to MKT gateway
     */
    public void sendConnectToMKTGwyFailureEvent() {
        ConnectToMKTGwyFailure connectToMKTGwyFailure = MDFEventGenerator.genConnectToMKTGwyFailure(TRADING_MDF_ID, TRADING_MDF_NAME);
        logger.info(connectToMKTGwyFailure.toString());
        eventProducer.sendEvent(connectToMKTGwyFailure);
    }

}
