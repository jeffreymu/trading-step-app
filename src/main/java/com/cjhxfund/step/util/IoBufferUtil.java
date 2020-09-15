package com.cjhxfund.step.util;

import org.apache.mina.core.buffer.IoBuffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class IoBufferUtil {

    public static String byteToString(Charset charset, byte[] data) {
        CharBuffer buffer = charset.decode(ByteBuffer.wrap(data));
        String str = new String(buffer.array());
        return str;
    }


    public static IoBuffer stringToIoBuffer(String str) {
        byte[] bt = str.getBytes();
        return byteToIoBuffer(bt);
    }


    public static IoBuffer byteToIoBuffer(byte[] bt) {
        IoBuffer ioBuffer = IoBuffer.allocate(bt.length);
        ioBuffer.put(bt, 0, bt.length);
        ioBuffer.flip();
        return ioBuffer;
    }


    public static byte[] ioBufferToByte(IoBuffer buf) {
        return buf.array();
    }


    public static String ioBufferToString(Charset charset, IoBuffer buf) {
        return byteToString(charset, ioBufferToByte(buf));
    }


    public static String ioBufferToString(IoBuffer buf) {
        return new String(ioBufferToByte(buf));
    }

}
