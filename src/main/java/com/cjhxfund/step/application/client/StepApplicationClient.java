package com.cjhxfund.step.application.client;

import com.cjhxfund.common.core.api.Tick;
import com.cjhxfund.common.disruptor.BaseDisruptorWrapper;
import com.cjhxfund.step.application.conn.MktGatewayConnector;
import com.cjhxfund.step.application.gateway.MktGatewayManager;
import com.cjhxfund.step.application.handler.MktMessageHandler;
import com.cjhxfund.step.event.StepEventManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.valencia.quotation.common.constant.MktConnMode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Launch a STEP application of client to publish ticks from gateway
 */
public class StepApplicationClient {

    private static Logger log = Logger.getLogger(StepApplicationClient.class);

    private String targetGwyHost = "";
    private int targetGwyPort = 8016;
    private int connMode = 1;
    private String localHost = "";
    private String userName = "cjhx";
    private String userPwd = "cjhx";

    @Autowired
    private StepEventManager stepEventManager;

    //private String senderCompID;
    //private String targetCompID;
    private MktGatewayManager mktGatewayManager;
    private MktGatewayConnector connector;
    private BaseDisruptorWrapper ringBuffer;
    private Map<String, Tick> ticks = new ConcurrentHashMap<String, Tick>();

    public void setMktGatewayManager(MktGatewayManager mktGatewayManager) {
        this.mktGatewayManager = mktGatewayManager;
    }

    /*public void run() {
        while (true) {
			log.info("Receiving SZ-HK connect ticks ...");
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (Exception e) {
				log.error("Error Msg: " + e.getMessage());
				log.error("Error Detail: ", e);
			}
		}
	}*/

    public void setTicks(Map<String, Tick> ticks) {
        this.ticks = ticks;
    }

    public void setRingBuffer(BaseDisruptorWrapper ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void start() {
        //MktGatewayManager.initProtocolConfig(this.senderCompID, this.targetCompID);
        if (connMode == 1) {
            connector = mktGatewayManager.createConnector(new MktMessageHandler(ringBuffer, ticks),
                    MktConnMode.MKT_CONN_MODE_TCP);
        } else {
            log.warn("Invalid connection mode");
        }
        boolean isConnected = false;
        try {
            isConnected = mktGatewayManager.connect2Gateway(connector, targetGwyHost, targetGwyPort, localHost);
        } catch (Exception ex) {
            log.error("Connect to gateway error: ", ex);
            isConnected = false;
        }
        if (isConnected) {
            boolean isLogon = mktGatewayManager.connectorLogin(connector, userName, userPwd);
            if (!isLogon)
                log.error("Logon to market data gateway failed");
        } else {
            log.error("Failed to connect to market data gateway " + targetGwyHost);
            if (stepEventManager != null) {
                stepEventManager.sendConnectToMKTGwyFailureEvent();
            }
            return;
        }
        log.info("Start step application");
    }

    public void stop() {
        mktGatewayManager.connectorLogout(connector);
        mktGatewayManager.destroyConnector(connector);
        connector = null;
        log.info("Stop step application");
    }

    public void setTargetGwyHost(String targetGwyHost) {
        this.targetGwyHost = targetGwyHost;
    }

    public void setTargetGwyPort(int targetGwyPort) {
        this.targetGwyPort = targetGwyPort;
    }

    public void setConnMode(int connMode) {
        this.connMode = connMode;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

}

