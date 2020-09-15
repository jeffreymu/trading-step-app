package com.cjhxfund.step.application.filter;

import org.valencia.quotation.common.step.business.MktDataSnapshot;
import org.valencia.quotation.common.step.field.*;
import org.valencia.quotation.common.step.field.*;

public class OldMessageInfo {
    private int tradSesMode;
    private int applID;
    private int applSeqNum;
    private String mDUpdateType;
    private int mDCount;
    private int rawDataLength;

    private String checkSum;

    public OldMessageInfo() {

    }

    public OldMessageInfo(MktDataSnapshot dataFull) throws Exception {
        setMessageParam(dataFull);
    }

    public void setMessageParam(MktDataSnapshot dataFull) throws Exception {
        tradSesMode = dataFull.getInt(TradSesMode.FIELD);
        applID = dataFull.getInt(ApplID.FIELD);
        applSeqNum = dataFull.getInt(ApplSeqNum.FIELD);
        mDUpdateType = dataFull.getString(MDUpdateType.FIELD);
        mDCount = dataFull.getInt(MDCount.FIELD);
        rawDataLength = dataFull.getInt(RawDataLength.FIELD);
    }

    public int getTradSesMode() {
        return tradSesMode;
    }

    public void setTradSesMode(int tradSesMode) {
        this.tradSesMode = tradSesMode;
    }

    public int getApplID() {
        return applID;
    }

    public void setApplID(int applID) {
        this.applID = applID;
    }

    public int getApplSeqNum() {
        return applSeqNum;
    }

    public void setApplSeqNum(int applSeqNum) {
        this.applSeqNum = applSeqNum;
    }

    public String getmDUpdateType() {
        return mDUpdateType;
    }

    public void setmDUpdateType(String mDUpdateType) {
        this.mDUpdateType = mDUpdateType;
    }

    public int getmDCount() {
        return mDCount;
    }

    public void setmDCount(int mDCount) {
        this.mDCount = mDCount;
    }

    public int getRawDataLength() {
        return rawDataLength;
    }

    public void setRawDataLength(int rawDataLength) {
        this.rawDataLength = rawDataLength;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

}
