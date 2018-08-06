package com.packetsniffer.emenegal.packetsniffer.tls;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class ServerNameIndicationExtension {

    private static final byte HOST_NAME = 10;

    /**
     * uint16
     */
    private short length;

    /**
     * uint8
     * host_name ( 10 )
     */
    private byte type;

    /**
     * uint16
     */
    private short serverNameLength;

    /**
     * Here we find the name of the server used be the client in the browser
     */
    private byte[] serverName;


    public ServerNameIndicationExtension(ByteBuffer stream){
        length = stream.getShort();
        type = stream.get();
        serverNameLength = stream.getShort();
        serverName = new byte[serverNameLength];
        stream.get(serverName,0,serverNameLength);
    }

    public int getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getServerNameLength() {
        return serverNameLength;
    }

    public void setServerNameLength(short serverNameLength) {
        this.serverNameLength = serverNameLength;
    }

    public byte[] getServerName() {
        return serverName;
    }

    public void setServerName(byte[] serverName) {
        this.serverName = serverName;
    }
}
