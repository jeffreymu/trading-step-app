package com.cjhxfund.step.application.codec;

import org.valencia.quotation.common.step.business.MktDataBody;
import org.valencia.quotation.common.step.business.MktDataSnapshot;

import java.io.Serializable;
import java.util.List;


public class FastMessageExpress implements Serializable {
    private static final long serialVersionUID = 1L;

    private MktDataSnapshot mktDataSnapshot;

    private List<MktDataBody> mktDataBody;

    public FastMessageExpress() {

    }

    public FastMessageExpress(MktDataSnapshot mktDataSnapshot, List<MktDataBody> mktDataBody) {
        this.mktDataSnapshot = mktDataSnapshot;
        this.mktDataBody = mktDataBody;
    }

    public MktDataSnapshot getMktDataSnapshot() {
        return mktDataSnapshot;
    }

    public void setMktDataSnapshot(MktDataSnapshot mktDataSnapshot) {
        this.mktDataSnapshot = mktDataSnapshot;
    }

    public List<MktDataBody> getmktDataBody() {
        return mktDataBody;
    }

    public void setmktDataBody(List<MktDataBody> mktDataBody) {
        this.mktDataBody = mktDataBody;
    }

    @Override
    public String toString() {
        if (mktDataSnapshot != null && mktDataBody != null) {
            return mktDataSnapshot.toString() + mktDataBody.toString();
        }
        return "";
    }

}
