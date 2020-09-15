package com.cjhxfund.step.application.heartbeat;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

public class KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler {
    private static Logger log = Logger.getLogger(KeepAliveRequestTimeoutHandlerImpl.class);

    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter arg0, IoSession arg1) throws Exception {
        log.info("Heartbeat timeout");
    }
}
