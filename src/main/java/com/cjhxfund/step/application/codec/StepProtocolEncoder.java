package com.cjhxfund.step.application.codec;

import org.valencia.quotation.common.config.StepCommonConfig;
import org.valencia.quotation.common.parser.STEPParser;
import org.valencia.quotation.common.step.FieldNotFound;
import org.valencia.quotation.common.step.IncorrectDataFormat;
import org.valencia.quotation.common.step.IncorrectTagValue;
import org.valencia.quotation.common.step.Message;
import org.valencia.quotation.util.IoBufferUtil;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

/**
 * STEP message encoder
 */
public class StepProtocolEncoder extends ProtocolEncoderAdapter {
    private static Logger log = Logger.getLogger(StepProtocolEncoder.class);

    private Charset charset;

    public StepProtocolEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        Message msg = (Message) message;
        STEPParser app = StepCommonConfig.getInstance().getStep();

        String step = null;
        try {
            step = app.toRawStr(msg);
            IoBuffer ioBuf = IoBufferUtil.stringToIoBuffer(step);
            out.write(ioBuf);
        } catch (IncorrectTagValue e) {
            log.info(e.getMessage());
        } catch (FieldNotFound e) {
            log.info(e.getMessage());
        } catch (IncorrectDataFormat e) {
            log.info(e.getMessage());
        }
    }
}
