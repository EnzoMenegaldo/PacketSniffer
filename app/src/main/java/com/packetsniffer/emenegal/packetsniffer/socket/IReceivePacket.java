package com.packetsniffer.emenegal.packetsniffer.socket;

import com.packetsniffer.emenegal.packetsniffer.packet.Packet;

public interface IReceivePacket {
	void receive(byte[] packet);
	void receive(Packet packet);
}
