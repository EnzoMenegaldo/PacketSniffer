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

package com.packetsniffer.emenegal.packetsniffer;


import android.content.pm.PackageManager;
import android.support.annotation.NonNull;


import com.packetsniffer.emenegal.packetsniffer.network.ip.IPv4Header;
import com.packetsniffer.emenegal.packetsniffer.*;
import com.packetsniffer.emenegal.packetsniffer.transport.ITransportHeader;
import com.packetsniffer.emenegal.packetsniffer.transport.tcp.TCPHeader;
import com.packetsniffer.emenegal.packetsniffer.transport.udp.UDPHeader;
import com.packetsniffer.emenegal.packetsniffer.util.PacketUtil;


import java.text.DateFormat;
import java.util.Date;

/**
 * Data structure that encapsulate both IPv4Header and TCPHeader
 * @author Borey Sao
 * Date: May 27, 2014
 */
public class Packet {
	@NonNull private final IPv4Header ipHeader;
	@NonNull private final ITransportHeader transportHeader;
	@NonNull private final byte[] buffer;
	private int applicationID;
	private final String applicationName;

	@NonNull private String hostName;
	@NonNull private final Date time;


	public Packet(@NonNull IPv4Header ipHeader, @NonNull ITransportHeader transportHeader, @NonNull byte[] data) {
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


		if(applicationID > 0)
			applicationName =  PacketSnifferService.PackageManager.getNameForUid(applicationID);
		else
			applicationName = "unknown";

		time = new Date();


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

	/**
	 * the whole packet data as an array of byte
	 * @return byte[]
	 */
	@NonNull
	public byte[] getBuffer() {
		return buffer;
	}
	

}
