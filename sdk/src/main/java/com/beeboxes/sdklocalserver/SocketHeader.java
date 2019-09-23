package com.beeboxes.sdklocalserver;

public class SocketHeader {
    //协议包头和奇偶校验
    public final static byte[] PICTURE_PACKAGE_HEAD = {(byte) 0xFF, (byte) 0xCF,
            (byte) 0xFA, (byte) 0xBF, (byte) 0xF6, (byte) 0xAF, (byte) 0xFE,
            (byte) 0xFF};
    public final static byte[] PICTURE_PACKAGE_END = {(byte) 0xEF, (byte) 0xDA,
            (byte) 0xFF, (byte) 0xFD};
}
