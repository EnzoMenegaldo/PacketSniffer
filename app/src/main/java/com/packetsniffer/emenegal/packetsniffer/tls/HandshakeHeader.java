package com.packetsniffer.emenegal.packetsniffer.tls;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class HandshakeHeader {

    public static final byte CLIENT_HELLO = 1;

    /**
     * uint8
     * Hello Request (0, 0x00)
     * Client Hello (1, 0x01)
     * Server Hello (2, 0x02)
     * Certificate (11, 0x0B)
     * Server Key Exchange (12, 0x0C)
     * Certificate Request (13, 0x0D)
     * Server Hello Done (14, 0x0E)
     * Certificate Verify (15, 0x0F)
     * Client Key Exchange (16, 0x10)
     * Finished (20, 0x14)
     */
    private byte handshakeProtocol;

    /**
     * 3bytes
     */
    private byte[] length;

    /**
     * uint16
     * SSLv3 ( 0x0300 )
     * TLSv1 ( 0x0301 )
     * SSLv2 ( 0x0002 never used )
     */
    private short version;

    /**
     * 32 bytes
     */
    private byte[] random;

    /**
     * uint8
     */
    private byte sessionIDLength;

    /**
     * The ID of a session the client wishes to use for this connection.
     * In the first Client Hello of the exchange, the session ID is empty
     */
    private byte[] sessionID;

    /**
     * uint16
     */
    private short cypherSuiteLength;

    /**
     * This is passed from the client to the server in the Client Hello message.
     * It contains the combinations of cryptographic algorithms supported by the client in order of the client's preference (first choice first).
     * Each cipher suite defines both a key exchange algorithm and a cipher spec.
     * The server selects a cipher suite or, if no acceptable choices are presented, returns a handshake failure alert and closes the connection.
     */
    private byte[] cypherSuite;

    /**
     * uint8
     */
    private byte compressionMethodLength;

    /**
     * Includes a list of compression algorithms supported by the client.
     * If the server does not support any method sent by the client, the connection fails.
     * The compression method can also be null
     */
    private byte[] compressionMethod;

    /**
     * uint16
     */
    private short extensionsLength;

    private List<Extension> extensions;

    public HandshakeHeader(ByteBuffer stream){
        handshakeProtocol = stream.get();
        length = new byte[3];
        stream.get(length,0,3);
        extensions = new ArrayList<>();
        if(isFirstHandshakePacket()) {
            version = stream.getShort();
            random = new byte[32];
            stream.get(random, 0, random.length);
            sessionIDLength = stream.get();
            sessionID = new byte[sessionIDLength];
            stream.get(sessionID, 0, sessionID.length);
            cypherSuiteLength = stream.getShort();
            cypherSuite = new byte[cypherSuiteLength];
            stream.get(cypherSuite, 0, cypherSuite.length);
            compressionMethodLength = stream.get();
            compressionMethod = new byte[compressionMethodLength];
            stream.get(compressionMethod, 0, compressionMethod.length);

            if(stream.hasRemaining())
                extensionsLength = stream.getShort();

            while (stream.hasRemaining()) {
                //We check if we have at least 4 bytes : 2 for the type and 2 for the length of the extension
                if(stream.remaining() > 4){
                    short type = stream.getShort();
                    short length = stream.getShort();
                    //A packet may have too many extension and so it might be fragmented
                    //Before creating a new extension, we check the number of byte remaining in the buffer to be sure the whole extension is in the buffer.
                    if(length <= stream.remaining()){
                        if (type == Extension.SERVER_NAME_EXTENSION)
                            extensions.add(new ServerNameExtension(stream, type, length));
                        else
                            extensions.add(new Extension(stream, type, length));
                    }else{
                        //We discard the rest of the data
                        stream.position(stream.limit());
                    }
                }else{
                    //We discard the rest of the data
                    stream.position(stream.limit());
                }
            }
        }

    }

    public byte getHandshakeProtocol() {
        return handshakeProtocol;
    }

    public void setHandshakeProtocol(byte handshakeProtocol) {
        this.handshakeProtocol = handshakeProtocol;
    }

    public byte[] getLength() {
        return length;
    }

    public void setLength(byte[] length) {
        this.length = length;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public byte[] getRandom() {
        return random;
    }

    public void setRandom(byte[] random) {
        this.random = random;
    }

    public int getSessionIDLength() {
        return sessionIDLength;
    }

    public void setSessionIDLength(byte sessionIDLength) {
        this.sessionIDLength = sessionIDLength;
    }

    public byte[] getSessionID() {
        return sessionID;
    }

    public void setSessionID(byte[] sessionID) {
        this.sessionID = sessionID;
    }

    public int getCypherSuiteLength() {
        return cypherSuiteLength;
    }

    public void setCypherSuiteLength(short cypherSuiteLength) {
        this.cypherSuiteLength = cypherSuiteLength;
    }

    public byte[] getCypherSuite() {
        return cypherSuite;
    }

    public void setCypherSuite(byte[] cypherSuite) {
        this.cypherSuite = cypherSuite;
    }

    public byte getCompressionMethodLength() {
        return compressionMethodLength;
    }

    public void setCompressionMethodLength(byte compressionMethodLength) {
        this.compressionMethodLength = compressionMethodLength;
    }

    public byte[] getCompressionMethod() {
        return compressionMethod;
    }

    public void setCompressionMethod(byte[] compressionMethod) {
        this.compressionMethod = compressionMethod;
    }

    public int getExtensionsLength() {
        return extensionsLength;
    }

    public void setExtensionsLength(short extensionsLength) {
        this.extensionsLength = extensionsLength;
    }

    public List<Extension> getExtensions() {
        return extensions;
    }

    public void setExtensions(ArrayList<Extension> extensions) {
        this.extensions = extensions;
    }

    public boolean isFirstHandshakePacket(){
        if(handshakeProtocol == CLIENT_HELLO)
            return true;
        return false;

    }

    public ServerNameExtension getServerNameExtension(){
        for(Extension extension : extensions)
            if(extension instanceof ServerNameExtension)
                return (ServerNameExtension)extension;
        return null;
    }


}
