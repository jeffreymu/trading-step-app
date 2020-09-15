package com.cjhxfund.step.application.handler;

import com.cjhxfund.common.algo.api.ext.SecurityType;
import com.cjhxfund.common.core.api.ExchangeType;
import com.cjhxfund.common.core.api.Tick;
import com.cjhxfund.common.disruptor.BaseDisruptorWrapper;
import com.cjhxfund.common.util.api.DateUtils;
import com.cjhxfund.jydb.utils.DateTimeUtil;
import com.cjhxfund.step.application.codec.FastMessageConverter;
import org.valencia.quotation.common.constant.FastMessageConstant;
import org.valencia.quotation.common.fast.MKTDataEntry;
import org.valencia.quotation.common.fast.NoMDEntry;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Client message receiver handler to process ticks
 */
public class MktMessageHandler extends IoHandlerAdapter {
    private static Logger log = Logger.getLogger(MktMessageHandler.class);

    private Date today;

    public MktMessageHandler(BaseDisruptorWrapper disruptor,
                             Map<String, Tick> ticks) {
        this.disruptor = disruptor;
        this.ticks = ticks;
        this.today = DateTimeUtil.getShortDate(new Date());
    }

    private BaseDisruptorWrapper disruptor;
    private Map<String, Tick> ticks;

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log.debug("session created");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        log.debug("session opened");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message instanceof org.openfast.Message) {
            org.openfast.Message fastMessage = (org.openfast.Message) message;
            MKTDataEntry mktDataEntry = FastMessageConverter.convert2Entry(fastMessage);
            log.debug(mktDataEntry.toString());
            publishTick(mktDataEntry);
        } else
            return;
    }

    private void publishTick(MKTDataEntry mktDataEntry) {
        String symbol = mktDataEntry.getSecurityID();
        //Pair<ExchangeType, String> key = new Pair<ExchangeType, String>(ExchangeType.SHHKCONN, symbol);
        String key = symbol + "." + ExchangeType.SZHKCONN.getFIXValue();
        Tick tick = ticks.get(key);
        if (tick == null) {
            tick = generateTick(ExchangeType.SZHKCONN, symbol);
            if (tick == null) {
                log.error("Fail to find Tick of " + symbol + ".SZHK");
                return;
            }
        }

        tick.setDate(DateUtils.convert2String(today));
        tick.setTime(String.valueOf(mktDataEntry.getOrigTime()).substring(8));
        double preClosePrice = new BigDecimal(mktDataEntry.getPrevClosePx()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        tick.setPreClose(preClosePrice/10000);
        tick.setSettlement(0);
        tick.setPreSettlement(0);
        tick.setAccuVolume(mktDataEntry.getTotalVolumeTrade()/100);
        double totalValueTrade = new BigDecimal(mktDataEntry.getTotalValueTrade()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        tick.setAccuValue(totalValueTrade/10000);

        for (NoMDEntry noMDEntry : mktDataEntry.getNoMDEntries()) {
            // 0=买入; 1=卖出; 2=最近价; 4=开盘价;7=最高价; 8=最低价; xh=按盘价
            log.debug("noMDEntry: " + noMDEntry.toString());
            if (noMDEntry.getMdEntryType().equalsIgnoreCase(FastMessageConstant.MD_ENTRY_NOMINAL_PX)) {
                double _lastPrice = new BigDecimal(noMDEntry.getMdEntryPx()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (_lastPrice > 0)
                    tick.setLast(_lastPrice/1000000);
            }
            if (noMDEntry.getMdEntryType().equalsIgnoreCase(FastMessageConstant.MD_ENTRY_OPEN_PX)) {
                double openPrice = new BigDecimal(noMDEntry.getMdEntryPx()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (openPrice > 0)
                    tick.setOpen(openPrice/1000000);
            }
            if (noMDEntry.getMdEntryType().equalsIgnoreCase(FastMessageConstant.MD_ENTRY_HIGH_PX)) {
                double highPrice = new BigDecimal(noMDEntry.getMdEntryPx()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (highPrice > 0)
                    tick.setHigh(highPrice/1000000);
            }
            if (noMDEntry.getMdEntryType().equalsIgnoreCase(FastMessageConstant.MD_ENTRY_LOW_PX)) {
                double lowPrice = new BigDecimal(noMDEntry.getMdEntryPx()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (lowPrice > 0)
                    tick.setLow(lowPrice/1000000);
            }
            if (noMDEntry.getMdEntryType().equalsIgnoreCase(FastMessageConstant.MD_ENTRY_BUY_PX)) {
                double bidPrice = new BigDecimal(noMDEntry.getMdEntryPx()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                tick.setBidPrice(bidPrice/1000000);
                tick.setBidSize(noMDEntry.getMdEntrySize()/100);
            }
            if (noMDEntry.getMdEntryType().equalsIgnoreCase(FastMessageConstant.MD_ENTRY_SELL_PX)) {
                double askPrice = new BigDecimal(noMDEntry.getMdEntryPx()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                tick.setAskPrice(askPrice/1000000);
                tick.setAskSize(noMDEntry.getMdEntrySize()/100);
            }
        }
        if (preClosePrice != 0) {
            tick.setUpRatio((tick.getLast() - tick.getPreClose()) / tick.getPreClose());
        }
        log.debug(tick.toString());
        if (disruptor != null) {
            disruptor.publishTick(tick);
            log.info("Publish SZ-HK connect ticks");
        }
    }

    private String formatTickTime(String tickFullTime) {
        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyyMMdd");
        String fullTickTime = defaultFormat.format(today) + tickFullTime.substring(8);
        Date dateTime = DateUtils.string2TickFullDate(fullTickTime);
        return DateUtils.getTime(dateTime);
    }

    private Tick generateTick(ExchangeType exchangeType, String symbol) {
        String isin = symbol + "." + exchangeType.getFIXValue();
        Tick tick = Tick.Factory.newInstance(symbol, isin, SecurityType.STOCK);
        tick.setDataProvider(ExchangeType.SZHKCONN.getFIXValue());
        //Pair<ExchangeType, String> key = new Pair<ExchangeType, String>(exchangeType, symbol);
        this.ticks.put(isin, tick);
        return tick;
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.debug("sending message");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.info("close session " + session.getConfig().toString() + " ok");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        log.info("session is idle");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error("session with exception: ", cause);
    }

}
