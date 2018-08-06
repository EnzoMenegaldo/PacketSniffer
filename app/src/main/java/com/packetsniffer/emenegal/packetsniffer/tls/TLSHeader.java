package com.packetsniffer.emenegal.packetsniffer.tls;

import java.nio.ByteBuffer;

public class TLSHeader {

    public static final byte HANDSHAKE = 22;
    public static final short TLSv1 = 0x0301;
    public static final short SSLv3 = 0x0300;

    //https://www.cisco.com/c/en/us/support/docs/security-vpn/secure-socket-layer-ssl/116181-technote-product-00.html
    /**
     * uint8
     * Handshake ( 22, 0x16 )
     * Change Cipher Spec ( 20, 0x14 )
     * Alert ( 21, 0x15 )
     * Application Data ( 23, 0x17 )
     */
    private byte type;

    /**
     * uint16
     * SSLv3 ( 0x0300 )
     * TLSv1 ( 0x0301 )
     * SSLv2 ( 0x0002 never used )
     */
    private short version;

    /**
     * uint16
     */
    private short length;


    private HandshakeHeader handshakeHeader;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public int getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public HandshakeHeader getHandshakeHeader() {
        return handshakeHeader;
    }

    public void setHandshakeHeader(HandshakeHeader handshakeHeader) {
        this.handshakeHeader = handshakeHeader;
    }

    public TLSHeader(ByteBuffer tcpPayload,byte type, short version){
        this.type = type;
        this.version = version;
        this.length = tcpPayload.getShort();

        if(type == HANDSHAKE){
            handshakeHeader = new HandshakeHeader(tcpPayload);
        }
    }

    public boolean isHandshakePacket(){
        return type == HANDSHAKE;
    }

    public boolean isFirstHandshakePacket(){
        if(isHandshakePacket())
            return handshakeHeader.isFirstHandshakePacket();
        return false;
    }
}
