package com.cjhxfund.step.application.gateway;

import com.cjhxfund.step.application.conn.AbstractClientAcceptor;
import com.cjhxfund.step.application.conn.AbstractClientConnector;
import com.cjhxfund.step.application.conn.MktGatewayConnector;
import com.cjhxfund.step.application.conn.tcp.TCPClientConnector;
import org.valencia.quotation.common.config.FastCommonConfig;
import org.valencia.quotation.common.config.StepCommonConfig;
import org.apache.mina.core.service.IoHandler;
import org.valencia.quotation.common.constant.MktConnMode;

/**
 * Gateway manager to connect to Market data gateway
 * which can accept client to connect to gateway via sending
 * step admin message, e.g logon, logout
 */
public class MktGatewayManager {

    private String senderCompID;
    private String targetCompID;

    public MktGatewayManager(String senderCompID, String targetCompID) {
        this.senderCompID = senderCompID;
        this.targetCompID = targetCompID;
    }

    public MktGatewayConnector createConnector(IoHandler handler, MktConnMode connMode) {
        if (connMode.equals(MktConnMode.MKT_CONN_MODE_TCP)) {
            return new TCPClientConnector(handler);
        }
        return null;
    }

    public void initProtocolConfig() {
        StepCommonConfig.getInstance().init(senderCompID, targetCompID);
        FastCommonConfig.getInstance().init();
    }

    public boolean destroyConnector(MktGatewayConnector connector) {
        if (connector != null) {
            connector.dispose();
            return true;
        }
        return false;
    }

    public boolean connect2Gateway(MktGatewayConnector connector, String host,
                                   int port, String localIP) throws Exception {

        return connect2Gateway(connector, host, port, localIP, -1);
    }

    public boolean connect2Gateway(MktGatewayConnector connector, String host,
                                   int port, String localIP, int timeout) throws Exception {
        if (connector != null) {
            if (connector instanceof AbstractClientConnector) {
                connector.connect(host, port, localIP, timeout);
                return true;
            } else if (connector instanceof AbstractClientAcceptor) {
                connector.connect(localIP, port, timeout);
                return true;
            }
        }
        return false;
    }

    public boolean connectorLogin(MktGatewayConnector connector, String userName, String password) {
        if (connector instanceof TCPClientConnector) {
            TCPClientConnector tcpClient = ((TCPClientConnector) connector);
            return tcpClient.sendLogonMessage(userName, password);
        }
        return false;
    }

    public boolean connectorLogout(MktGatewayConnector connector) {
        if (connector instanceof TCPClientConnector) {
            TCPClientConnector tcpClient = ((TCPClientConnector) connector);
            return tcpClient.sendLogoutMessage();
        }
        return false;
    }

}
