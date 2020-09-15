package com.cjhxfund.step.application.filter;

import org.valencia.quotation.common.constant.StepMessageConstant;
import org.valencia.quotation.common.step.business.Logon;
import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

public class LogonFilter extends IoFilterAdapter {
    private static Logger log = Logger.getLogger(LogonFilter.class);

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (message instanceof Logon) {
            FilterLock lock = (FilterLock) session.getAttribute(StepMessageConstant.SESSION_LOCK);
            try {
                synchronized (lock) {
                    lock.setOk(true);
                    lock.notify();
                }
            } catch (Exception e) {
                log.error("Logon error: ", e);
            }
            log.info("Market data receiver logon success");
        } else {
            nextFilter.messageReceived(session, message);
        }
    }

}
