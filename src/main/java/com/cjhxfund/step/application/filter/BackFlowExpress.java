package com.cjhxfund.step.application.filter;

import org.valencia.quotation.common.constant.StepMessageConstant;
import org.valencia.quotation.common.step.business.MktDataSnapshot;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;

public class BackFlowExpress {

    Map<String, OldMessageInfo> securityTypeMap = new HashMap<String, OldMessageInfo>();

    public BackFlowExpress() {

    }

    public static synchronized BackFlowExpress getBackFlowExpressBySession(IoSession session) {
        BackFlowExpress backFlowObj = (BackFlowExpress) session.getAttribute(StepMessageConstant.SESSION_BACKFLOW);
        if (backFlowObj == null) {
            backFlowObj = new BackFlowExpress();
            session.setAttribute(StepMessageConstant.SESSION_BACKFLOW, backFlowObj);
        }
        return backFlowObj;
    }

    public OldMessageInfo getOldMessageInfo(String securityType) {
        return securityTypeMap.get(securityType);
    }

    public synchronized OldMessageInfo getNewMessageinfo(String securityType, MktDataSnapshot dataFull) throws Exception {
        OldMessageInfo newInfo = new OldMessageInfo(dataFull);
        securityTypeMap.put(securityType, newInfo);
        return newInfo;
    }

}
