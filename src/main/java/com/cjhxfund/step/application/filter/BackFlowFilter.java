package com.cjhxfund.step.application.filter;

import com.cjhxfund.step.application.codec.FastMessageExpress;
import org.valencia.quotation.common.step.field.ApplID;
import org.valencia.quotation.common.step.business.MktDataSnapshot;
import org.valencia.quotation.common.step.field.ApplSeqNum;
import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

public class BackFlowFilter extends IoFilterAdapter {
    private static Logger log = Logger.getLogger(BackFlowFilter.class);

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (message instanceof FastMessageExpress) {
            FastMessageExpress sepMessage = (FastMessageExpress) message;
            if (checkBackFlow(sepMessage, session)) {
                nextFilter.messageReceived(session, message);
            } else {
                log.info("ApplSeqNum : " + sepMessage.getMktDataSnapshot().getApplSeqNum());
            }
        } else {
            nextFilter.messageReceived(session, message);
        }
    }

    private boolean checkBackFlow(FastMessageExpress message, IoSession session) throws Exception {
        MktDataSnapshot dataFull = message.getMktDataSnapshot();
        String securityType = dataFull.getSecurityType().getValue();
        BackFlowExpress backFlowObj = BackFlowExpress.getBackFlowExpressBySession(session);

        OldMessageInfo oldMessageInfo = backFlowObj.getOldMessageInfo(securityType);
        boolean isCheckOk = false;
        if (oldMessageInfo == null) {
            oldMessageInfo = backFlowObj.getNewMessageinfo(securityType, dataFull);
            isCheckOk = true;
        } else {
            if (oldMessageInfo.getApplID() == dataFull.getInt(ApplID.FIELD)) {
                if (oldMessageInfo.getApplSeqNum() < dataFull.getInt(ApplSeqNum.FIELD)) {
                    isCheckOk = true;
                    oldMessageInfo.setMessageParam(dataFull);
                }
            } else {
                isCheckOk = true;
                oldMessageInfo.setMessageParam(dataFull);
            }
        }
        return isCheckOk;
    }

}
