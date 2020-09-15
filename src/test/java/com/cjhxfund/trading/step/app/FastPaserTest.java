package com.cjhxfund.trading.step.app;

import com.cjhxfund.step.application.codec.FastMessageConverter;
import org.valencia.quotation.common.config.FastCommonConfig;
import org.valencia.quotation.common.fast.MKTDataEntry;
import org.openfast.codec.FastDecoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FastPaserTest {

    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length
                && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset != buffer.length) {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }
        fi.close();
        return buffer;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "D:/step.dat";
        byte[] fastBytes = getContent(fileName);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fastBytes);
        FastCommonConfig.getInstance().init();
        FastDecoder decoder = new FastDecoder(FastCommonConfig.getInstance().getContext(), byteArrayInputStream);
        org.openfast.Message message = decoder.readMessage();
        decoder.reset();
        System.out.println(message.toString());
        MKTDataEntry mktDataEntry = FastMessageConverter.convert2Entry(message);
        System.out.println("");
    }
}
