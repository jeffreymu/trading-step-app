package com.cjhxfund.step.application.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

public class StepCodecFactory implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public StepCodecFactory(Charset charset) {
        encoder = new StepProtocolEncoder(charset);
        decoder = new StepProtocolDecoder(charset);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

}
