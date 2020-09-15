package com.cjhxfund.step.application.filter;

import org.valencia.quotation.common.constant.StepMessageConstant;
import org.valencia.quotation.common.step.business.Logout;
import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;


public class LogoutFilter extends IoFilterAdapter {
    private static Logger log = Logger.getLogger(LogoutFilter.class);

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (message instanceof Logout) {
            FilterLock lock = (FilterLock) session.getAttribute(StepMessageConstant.SESSION_LOCK);
            session.close(true);
            try {
                synchronized (lock) {
                    lock.setOk(true);
                    lock.notify();
                }
            } catch (Exception e) {
                log.error("Logout error: ", e);
            }
            log.info("Market data receiver logout success");
        } else {
            nextFilter.messageReceived(session, message);
        }
    }
}
