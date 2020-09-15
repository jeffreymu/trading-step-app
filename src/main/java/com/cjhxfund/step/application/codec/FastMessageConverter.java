package com.cjhxfund.step.application.codec;


import org.valencia.quotation.common.constant.FastMessageConstant;
import org.valencia.quotation.common.fast.MKTDataEntry;
import org.valencia.quotation.common.fast.NoMDEntry;
import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastMessageConverter {
    private static Logger logger = LoggerFactory.getLogger(FastMessageConverter.class);

    public static MKTDataEntry convert2Entry(Message message) {
        MKTDataEntry mktDataEntry = new MKTDataEntry();
        if (message == null) return null;
        try {
            mktDataEntry.setTemplateID(message.getTemplate().getId());
            mktDataEntry.setChannelID(message.getString(FastMessageConstant.MD_ENTRY_CHANNELNO));
            mktDataEntry.setOrigTime(message.getLong(FastMessageConstant.MD_ENTRY_ORIGTIME));
            mktDataEntry.setPrevClosePx(message.getLong(FastMessageConstant.MD_ENTRY_PRECLOSEPX));
            mktDataEntry.setSecurityID(message.getString(FastMessageConstant.MD_ENTRY_SECURITYID));
            mktDataEntry.setNumTrades(message.getLong(FastMessageConstant.MD_ENTRY_NUMTRADES));
            mktDataEntry.setTotalVolumeTrade(message.getLong(FastMessageConstant.MD_ENTRY_TOTALVOLUMETRADE));
            mktDataEntry.setTotalValueTrade(message.getLong(FastMessageConstant.MD_ENTRY_TOTALVALUETRADE));
            SequenceValue values = message.getSequence(FastMessageConstant.MD_ENTRY_NOMDENTRIES);
            if (values != null) {
                for (int i = 0; i < values.getLength(); i++) {
                    NoMDEntry noMDEntry = new NoMDEntry();
                    GroupValue g = values.get(i);
                    noMDEntry.setMdEntryType(g.getString(FastMessageConstant.MD_ENTRY_MDENTRYTYPE));
                    noMDEntry.setMdEntryPx(g.getDouble(FastMessageConstant.MD_ENTRY_MDENTRYPX));
                    noMDEntry.setMdEntrySize(g.getLong(FastMessageConstant.MD_ENTRY_MDENTRYSIZE));
                    noMDEntry.setMdPriceLevel(g.getInt(FastMessageConstant.MD_ENTRY_MDPRICELEVEL));
                    mktDataEntry.addNoMDEntry(noMDEntry);
                }
            }
        } catch (UnsupportedOperationException e) {
        }
        return mktDataEntry;
    }

    public static void printMessage(Message message) {
        if (message == null) return;

        logger.info("TemplateID: " + message.getTemplate().getId());
        logger.info("ChannelNo: " + message.getString("ChannelNo"));
        logger.info("OrigTime: " + message.getLong("OrigTime"));
        logger.info("SecurityID: " + message.getString("SecurityID"));
        logger.info("PreClosePx: " + message.getLong("PreClosePx"));
        logger.info("NumTrades: " + message.getLong("NumTrades"));
        SequenceValue values = message.getSequence("NoMDEntries");
        if (values != null) {
            logger.info("Sequence value number is: " + values.getLength());
            for (int i = 0; i < values.getLength(); i++) {
                GroupValue g = values.get(i);
                logger.info("  MDEntryPx: " + g.getLong("MDEntryPx"));
            }
        } else {
            logger.info("Sequence is null");
        }
    }

}
