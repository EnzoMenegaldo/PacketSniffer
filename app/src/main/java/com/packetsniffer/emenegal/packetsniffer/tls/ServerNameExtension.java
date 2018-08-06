package com.packetsniffer.emenegal.packetsniffer.tls;

import java.nio.ByteBuffer;

public class ServerNameExtension extends Extension {

    private ServerNameIndicationExtension serverNameIndicationExtension;

    public ServerNameExtension(ByteBuffer stream, short type, short length){
        super(stream,type,length);
        serverNameIndicationExtension = new ServerNameIndicationExtension(ByteBuffer.wrap(dataExtension));

    }

    public ServerNameIndicationExtension getServerNameIndicationExtension() {
        return serverNameIndicationExtension;
    }

    public void setServerNameIndicationExtension(ServerNameIndicationExtension serverNameIndicationExtension) {
        this.serverNameIndicationExtension = serverNameIndicationExtension;
    }


}
