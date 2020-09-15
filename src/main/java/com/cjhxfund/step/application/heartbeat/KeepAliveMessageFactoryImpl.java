package com.cjhxfund.step.application.heartbeat;

import org.valencia.quotation.common.config.StepCommonConfig;
import org.valencia.quotation.common.parser.STEPParser;
import org.valencia.quotation.common.step.Message;
import org.valencia.quotation.common.step.business.Heartbeat;
import org.valencia.quotation.common.util.StepMessageUtil;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
    private static Logger log = Logger.getLogger(KeepAliveMessageFactoryImpl.class);

    @Override
    public Object getRequest(IoSession session) {
        STEPParser app = StepCommonConfig.getInstance().getStep();
        Message heartbeat = StepMessageUtil.genHeartbeatMessage(app);
        log.info("HeartBeat Outgoing: " + heartbeat);
        return heartbeat;
    }

    @Override
    public Object getResponse(IoSession session, Object message) {
        return message;
    }

    @Override
    public boolean isRequest(IoSession session, Object message) {
        return false;
    }

    @Override
    public boolean isResponse(IoSession session, Object message) {
        if (message instanceof Heartbeat) {
            log.info("HeartBeat Incoming: " + message.toString());
            return true;
        }
        return false;
    }
}
