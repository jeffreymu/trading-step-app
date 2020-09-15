package com.cjhxfund.step.application.conn;

import org.apache.mina.core.service.IoHandler;

public interface MktGatewayConnector {

    void initConnector(IoHandler handler);

    void connect(String host, int port, int timeout) throws Exception;

    void connect(String host, int port, String localIP, int timeout) throws Exception;

    void dispose();

}
