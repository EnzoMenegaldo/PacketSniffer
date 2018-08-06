package com.packetsniffer.emenegal.packetsniffer.tls;

import java.nio.ByteBuffer;

public class Extension {

    public static final int SERVER_NAME_EXTENSION = 0;

    /**
     * uint16
     * server_name (0)
     * ec_point_formats (11)
     * supported_groups (10)
     * session ticket tls (35)
     * next_protocol_negotiation (13172)
     * ...
     * https://www.iana.org/assignments/tls-extensiontype-values/tls-extensiontype-values.xml
     */
    protected short type;

    /**
     * uint16
     */
    protected short length;

    protected byte[] dataExtension;

    public int getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }


    public Extension(ByteBuffer stream, short type, short length) {
        this.type = type;
        this.length = length;
        dataExtension = new byte[length];
        stream.get(dataExtension,0,length);
    }
}
