package com.cjhxfund.step.application.codec;

import org.valencia.quotation.common.step.Message;
import org.valencia.quotation.util.IoBufferUtil;
import org.valencia.quotation.common.config.FastCommonConfig;
import org.valencia.quotation.common.config.StepCommonConfig;
import org.valencia.quotation.common.constant.StepMessageConstant;
import org.valencia.quotation.common.util.MessageParseUtil;
import org.valencia.quotation.common.util.StepMessageUtil;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.charset.Charset;

/**
 * STEP message decoder
 */
public class StepProtocolDecoder extends CumulativeProtocolDecoder {

    private static Logger log = Logger.getLogger(StepProtocolDecoder.class);
    private Charset charset;
    private int totalLengthWithoutBody;
    private int afterBeginStrMinLength;

    public StepProtocolDecoder(Charset charset) {
        this.charset = charset;
        totalLengthWithoutBody = StepMessageConstant.BEGINSTRING_LENGTH_WITH_SPLIT
                + StepMessageConstant.SPLIT_BYTE_LENGTH
                + StepMessageConstant.CHECKSUM_LENGTH
                + StepMessageConstant.SPLIT_BYTE_LENGTH;
        afterBeginStrMinLength = StepMessageConstant.BODY_LENGTH_REGION_STRING
                + StepMessageConstant.REGION_VALUE_SPLIT_STRING.length()
                + StepMessageConstant.CHECKSUM_LENGTH
                + StepMessageConstant.SPLIT_BYTE_LENGTH * 2;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int position = 0;
        while (true) {
            if (in.remaining() < totalLengthWithoutBody) {
                break;
            }

            byte[] beginLengthByte = new byte[StepMessageConstant.BEGINSTRING_LENGTH_NOT_SPLIT];
            in.get(beginLengthByte);

            while (!(StepMessageConstant.BEGINSTRING.equals(new String(beginLengthByte)))) {
                position = position + 1;
                in.position(position);
                if (in.remaining() < totalLengthWithoutBody) {
                    break;
                }
                in.get(beginLengthByte);
            }

            if (in.remaining() < afterBeginStrMinLength) {
                in.position(position);
                break;
            }

            in.position(position + StepMessageConstant.BEGINSTRING_LENGTH_WITH_SPLIT);
            int bodyLengthByteLen = 0;
            while (in.get() != 1 && bodyLengthByteLen <= StepMessageConstant.MAX_BODY_LENGTH) {
                bodyLengthByteLen++;
            }

            in.position(position + StepMessageConstant.BEGINSTRING_LENGTH_WITH_SPLIT);
            byte[] bodyLengthByte = new byte[bodyLengthByteLen];
            in.get(bodyLengthByte);
            int bodyLength = 0;
            String bodyLenStr = new String(bodyLengthByte);
            String bodyLenArr[] = bodyLenStr.split(StepMessageConstant.REGION_VALUE_SPLIT_STRING);
            if (bodyLenArr.length == 2 && StepMessageConstant.BODY_LENGTH_REGION_STRING == Integer.parseInt(bodyLenArr[0])) {
                bodyLength = Integer.parseInt(bodyLenArr[1]);
            } else {
                break;
            }

            int totalLength = totalLengthWithoutBody + bodyLengthByteLen + bodyLength;
            in.position(position);
            if (in.remaining() < totalLength) {
                break;
            }
            byte[] totalLengthByte = new byte[totalLength];
            in.get(totalLengthByte);

            String messageData = IoBufferUtil.byteToString(charset, totalLengthByte);
            final String msgType = MessageParseUtil.getMessageType(messageData);

            if (StepMessageUtil.isMktSnapshotMessage(msgType)) {
                log.debug("Incoming: Step market data snapshot");
                //log.info("W: " + messageData.toString());
                org.openfast.Message message = FastCommonConfig.getInstance().getFast().parseMarketData(totalLengthByte);
                if (message != null)
                    out.write(message);
            } else if (StepMessageUtil.isStepAdminMessage(msgType)) {
                log.info("Incoming: " + messageData.toString());
                Message message = StepCommonConfig.getInstance().getStep().parseMessage(messageData);
                if (message != null)
                    out.write(message);
            } else {
                //log.warn("Not valid step message " + msgType);
                break;
            }
            position = position + totalLength;
        }

        return false;
    }

}
