package com.cjhxfund.step.application.conn.tcp;

import com.cjhxfund.step.application.codec.StepCodecFactory;
import com.cjhxfund.step.application.filter.FilterLock;
import com.cjhxfund.step.application.filter.LogonFilter;
import com.cjhxfund.step.application.filter.LogoutFilter;
import org.valencia.quotation.common.constant.MarketType;
import org.valencia.quotation.common.parser.STEPParser;
import org.valencia.quotation.common.step.Message;
import com.cjhxfund.step.application.conn.AbstractClientConnector;
import com.cjhxfund.step.application.health.StepHeartBeatDaemon;
import com.cjhxfund.step.application.heartbeat.KeepAliveMessageFactoryImpl;
import org.valencia.quotation.common.config.StepCommonConfig;
import org.valencia.quotation.common.constant.MktCommonEnum;
import org.valencia.quotation.common.constant.StepMessageConstant;
import org.valencia.quotation.common.util.StepMessageUtil;
import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Client connector with TCP mode to connect to gateway
 */
public class TCPClientConnector extends AbstractClientConnector {
    private static Logger log = Logger.getLogger(TCPClientConnector.class);

    private NioSocketConnector connector;
    private IoSession session;
    private StepHeartBeatDaemon stepHeartBeatDaemon;

    public TCPClientConnector(IoHandler handler) {
        initConnector(handler);
    }

    public synchronized void initConnector(IoHandler handler) {
        if (connector == null) {
            connector = new NioSocketConnector();
            connector.getSessionConfig().setTcpNoDelay(true);
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new StepCodecFactory(Charset.forName(StepMessageConstant.DECODE_CHARSET))));
            connector.getFilterChain().addLast("logonFilter", new LogonFilter());
            connector.getFilterChain().addLast("logoutFilter", new LogoutFilter());
            connector.getFilterChain().addLast("heartBeat", createHeartBeatFilter());
            connector.setHandler(handler);
        }
    }

    private KeepAliveFilter createHeartBeatFilter() {
        KeepAliveMessageFactory heartBeatFactory = new KeepAliveMessageFactoryImpl();
        KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE);
        heartBeat.setForwardEvent(true);
        heartBeat.setRequestInterval(30);
        return heartBeat;
    }

    @Override
    public void connect(String host, int port, String localIP, int timeout) throws Exception {
        timeout = timeout < 0 ? StepMessageConstant.RECEIVE_TIMEOUT : timeout;
        connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, timeout);
        connector.setConnectTimeoutMillis(StepMessageConstant.CONNECT_TIMEOUT);

        ConnectFuture future = null;
        if (null == localIP || "".equals(localIP.trim())) {
            future = connector.connect(new InetSocketAddress(host, port));
        } else {
            future = connector.connect(new InetSocketAddress(host, port), new InetSocketAddress(localIP, 0));
        }
        future.awaitUninterruptibly();

        session = future.getSession();
        session.setAttribute(StepMessageConstant.SESSION_LOCK, new FilterLock());

    }

    public boolean isLockOk() {
        if (session != null) {
            FilterLock lock = (FilterLock) session.getAttribute(StepMessageConstant.SESSION_LOCK);
            return lock.isOk();
        }
        return false;
    }

    public void doLock() throws InterruptedException {
        if (session != null) {
            FilterLock lock = (FilterLock) session.getAttribute(StepMessageConstant.SESSION_LOCK);
            synchronized (lock) {
                lock.setOk(false);
                lock.wait(StepMessageConstant.CONNECT_TIMEOUT * 1000L);
            }
        }
    }

    public boolean sendLogonMessage(String userName, String password) {
        STEPParser app = StepCommonConfig.getInstance().getStep();
        Message message = StepMessageUtil.genLogonMessage(app, StepMessageConstant.HEARTBEAT_RATE, userName, password);
        session.write(message);
        log.info("Outgoing: " + message.toString());
        try {
            doLock();
        } catch (InterruptedException e) {
            log.error("Send Logon Message Error: ", e);
        }
        if (isLockOk()) {
            stepHeartBeatDaemon = new StepHeartBeatDaemon(session);
        }
        return isLockOk();
    }

    public boolean sendLogoutMessage() {
        STEPParser app = StepCommonConfig.getInstance().getStep();
        Message message = StepMessageUtil.genLogoutMessage(app);
        session.write(message);
        log.info("Outgoing: " + message.toString());
        try {
            doLock();
        } catch (InterruptedException e) {
            log.error("Send Logout Message Error: ", e);
        }
        return false;
    }

    public void sendMarketMessage(MarketType mrkType) {
        STEPParser app = StepCommonConfig.getInstance().getStep();
        Message marketMessage = StepMessageUtil.genMarketSubscribeMessage(app, mrkType.getMkdRequest());
        session.write(marketMessage);
        log.info("Outgoing: " + marketMessage.toString());
    }

    public void dispose() {
        if (connector != null) {
            connector.dispose();
            connector = null;
        }
        if (session != null) {
            session.close(true);
            session = null;
        }
        if (stepHeartBeatDaemon != null) {
            stepHeartBeatDaemon.stopHeartBeat();
            stepHeartBeatDaemon = null;
        }
    }

}
