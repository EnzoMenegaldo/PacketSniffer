/*
 *  Copyright 2014 AT&T
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.packetsniffer.emenegal.packetsniffer.packet;


import android.content.pm.PackageManager;
import android.support.annotation.NonNull;


import com.packetsniffer.emenegal.packetsniffer.database.PacketModel;
import com.packetsniffer.emenegal.packetsniffer.network.ip.IPv4Header;
import com.packetsniffer.emenegal.packetsniffer.*;
import com.packetsniffer.emenegal.packetsniffer.packetRebuild.ByteUtils;
import com.packetsniffer.emenegal.packetsniffer.session.SessionHandler;
import com.packetsniffer.emenegal.packetsniffer.tls.ServerNameExtension;
import com.packetsniffer.emenegal.packetsniffer.tls.TLSHeader;
import com.packetsniffer.emenegal.packetsniffer.transport.ITransportHeader;
import com.packetsniffer.emenegal.packetsniffer.transport.tcp.TCPHeader;
import com.packetsniffer.emenegal.packetsniffer.transport.udp.UDPHeader;
import com.packetsniffer.emenegal.packetsniffer.util.HTTPUtil;
import com.packetsniffer.emenegal.packetsniffer.util.PacketUtil;


import org.apache.commons.httpclient.Header;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static org.apache.commons.httpclient.params.HttpMethodParams.HTTP_ELEMENT_CHARSET;

/**
 * Data structure that encapsulate both IPv4Header and TCPHeader
 * @author Borey Sao
 * Date: May 27, 2014
 */
public class Packet {
	public static final int HTTP_PORT = 80;
	public static final int HTTPS_PORT = 443;

	@NonNull private final IPv4Header ipHeader;
	@NonNull private final ITransportHeader transportHeader;
	@NonNull private final byte[] buffer;
	private int applicationID;
	private final String applicationName;

	@NonNull private String hostName;
	@NonNull private final Date time;

	private boolean isHttp;

	private boolean isHttps;

	private boolean inComing;

	private int tcpPayloadLength;

	public Packet(@NonNull IPv4Header ipHeader, @NonNull ITransportHeader transportHeader, @NonNull byte[] data, int tcpPayloadLength) {
		this.ipHeader = ipHeader;
		this.transportHeader = transportHeader;
		if (transportHeader instanceof TCPHeader) {
			applicationID = get_uid(this.ipHeader.getIpVersion(),this.ipHeader.getProtocol(),
										PacketUtil.intToIPAddress(this.ipHeader.getSourceIP()),transportHeader.getSourcePort(),
											PacketUtil.intToIPAddress(this.ipHeader.getDestinationIP()),transportHeader.getDestinationPort());
		} else if (transportHeader instanceof UDPHeader) {
			applicationID = get_uid(this.ipHeader.getIpVersion(),this.ipHeader.getProtocol(),
										PacketUtil.intToIPAddress(this.ipHeader.getSourceIP()),transportHeader.getSourcePort(),
											PacketUtil.intToIPAddress(this.ipHeader.getDestinationIP()),transportHeader.getDestinationPort());
		}
		buffer = data;

		this.tcpPayloadLength = tcpPayloadLength;

		if(applicationID > 0)
			applicationName =  PacketSnifferService.PackageManager.getNameForUid(applicationID);
		else
			applicationName = "unknown";

		time = new Date();
		isHttp = false;
		isHttps = false;

		if(PacketUtil.intToIPAddress(ipHeader.getDestinationIP()).equals(PacketSnifferService.IP_ADDRESS))
			inComing = true;

		if(transportHeader instanceof TCPHeader) {
			checkHTTProtocol();
			checkTLSProtocol();
		}
	}

	/**
	 @return the uid of the application which has sent this packet
	 */
	private native int get_uid(int ipVersion, int protocol, String sourceIP, int sourcePort, String destinationIP, int destinationPort);

	public byte getProtocol() {
		return ipHeader.getProtocol();
	}

	@NonNull
	public ITransportHeader getTransportHeader() {
		return transportHeader;
	}

	public int getSourcePort() {
		return transportHeader.getSourcePort();
	}

	public int getDestinationPort() {
		return transportHeader.getDestinationPort();
	}

	@NonNull
	public String getTime(DateFormat dateFormat){
		return dateFormat.format(new Date());
	}

	@NonNull
	public Date getTime(){
		return time;
	}

	@NonNull
	public IPv4Header getIpHeader() {
		return ipHeader;
	}

	public int getApplicationID(){
		return applicationID;
	}

	public String getApplicationName() {
		return applicationName;
	}

	@NonNull
	public String getHostName(){return  hostName;}

	public void setHostName(@NonNull String hostName) {
		this.hostName = hostName;
	}

	public boolean isHttp() {
		return isHttp;
	}

	public void setHttp(boolean http) {
		isHttp = http;
	}

	public boolean isHttps() {
		return isHttps;
	}

	public void setHttps(boolean https) {
		isHttps = https;
	}

	public boolean isInComing() {
		return inComing;
	}

	public void setInComing(boolean inComing) {
		this.inComing = inComing;
	}

	/**
	 * the whole packet data as an array of byte
	 * @return byte[]
	 */
	@NonNull
	public byte[] getBuffer() {
		return buffer;
	}
	
	public PacketModel getPacketModel(){
		PacketModel packetModel = new PacketModel();
		packetModel.setApplication(applicationName);
		packetModel.setDate(time);
		packetModel.setHostname(hostName);
		packetModel.setIpDestination(PacketUtil.intToIPAddress(ipHeader.getDestinationIP()));
		return packetModel;
	}

	/**
	 * First we check whether the packet is a TLSPacket or not.
	 * If it's actually a TLSPacket then we check if it's the first packet of the protocol.
	 * If it is then we can get the server name and finally we save this packet in the database .
	 */
	public boolean checkTLSProtocol(){
		if(transportHeader.getDestinationPort() == HTTPS_PORT){
			int headerLength = ipHeader.getIPHeaderLength()+((TCPHeader)transportHeader).getTCPHeaderLength();
			ByteBuffer byteBuffer = ByteBuffer.wrap(buffer,headerLength,tcpPayloadLength);
			if(byteBuffer.hasRemaining()) {
				byte type = byteBuffer.get();
				if(type == TLSHeader.HANDSHAKE){
					short version = byteBuffer.getShort();
					//We check if it's a SSL protocol
					if(version == TLSHeader.SSLv3 || version == TLSHeader.TLSv1) {
						TLSHeader tlsHeader = new TLSHeader(byteBuffer, type, version);
						if (tlsHeader.isFirstHandshakePacket()) {
							ServerNameExtension serverNameExtension = tlsHeader.getHandshakeHeader().getServerNameExtension();
							// if there is a server name extension,we get the server name and we create a packet with these information then we save it in the DataBase
							if (serverNameExtension != null) {
								String serverName = new String(serverNameExtension.getServerNameIndicationExtension().getServerName(), Charset.forName("UTF-8"));
								if(PacketUtil.isInterestingServerName(serverName) && PacketUtil.isNewConnection(serverName)) {
									this.setHostName(serverName);
									isHttps = true;
								}
							}
						}
					}
				}
			}
			return true;
		}else
			return false;
	}

	/**
	 * We look into each packet to find those containing http protocol
	 */
	public void checkHTTProtocol(){
		//Avoid packet which are destined to another port than 80
		if(transportHeader.getDestinationPort() == HTTP_PORT){
			int headerLength = ipHeader.getIPHeaderLength()+((TCPHeader)transportHeader).getTCPHeaderLength();
			ByteBuffer byteBuffer = ByteBuffer.wrap(buffer,headerLength,tcpPayloadLength);
			if(byteBuffer.hasRemaining()) {
				try {
					ByteBuffer tmpBuffer = byteBuffer.slice();
					List<Header> headers = HTTPUtil.parseHeaders(new ByteArrayInputStream(tmpBuffer.array()),HTTP_ELEMENT_CHARSET);
					for(Header header : headers){
						if(header.getName().equals("Host")){
							//Add the packet to the DataBase
							if(PacketUtil.isInterestingServerName(header.getValue())){
								this.setHostName(header.getValue());
								isHttp = true;
							}
							break;
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
