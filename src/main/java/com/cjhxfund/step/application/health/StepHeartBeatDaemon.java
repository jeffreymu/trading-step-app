package com.cjhxfund.step.application.health;

import org.valencia.quotation.common.parser.STEPParser;
import org.valencia.quotation.common.step.Message;
import org.valencia.quotation.common.config.StepCommonConfig;
import org.valencia.quotation.common.constant.StepMessageConstant;
import org.valencia.quotation.common.util.StepMessageUtil;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

public class StepHeartBeatDaemon {
    private static final Logger log = Logger.getLogger(StepHeartBeatDaemon.class);

    private SimpleTimer timer;
    private IoSession session;
    private boolean isStopTask = false;

    public StepHeartBeatDaemon(IoSession session) {
        this.session = session;
        isStopTask = false;
        timer = new SimpleTimer("HeartBeat Timer");
        timer.schedule(new HeartBeatTask(), 0, StepMessageConstant.HEARTBEAT_RATE * 1000);
    }

    public void stopHeartBeat() {
        timer.cancel();
        log.debug("Heartbeat stop");
        isStopTask = true;
    }

    private class HeartBeatTask extends SimpleTimerTask {

        @Override
        public void run() {
            if (session == null) {
                return;
            }
            if (isStopTask) return;
            STEPParser app = StepCommonConfig.getInstance().getStep();
            Message heartbeat = StepMessageUtil.genHeartbeatMessage(app);
            session.write(heartbeat);
            log.info("Outgoing: " + heartbeat.toString());
        }
    }
}
