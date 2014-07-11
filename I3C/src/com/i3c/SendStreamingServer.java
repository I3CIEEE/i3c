package com.i3c;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendStreamingServer extends Thread {
	byte[] output;
	DatagramSocket s;

	SendStreamingServer(byte[] out, DatagramSocket s) {
		this.output = out;
		this.s = s;
	}

	public void run() {
		try {
			int port = 14000; // Ip y puerto StremingServer
			String host = "193.147.73.208";
			
			InetAddress Ip = InetAddress.getByName(host);
			DatagramPacket sendPacket = new DatagramPacket(output,
					output.length, Ip, port);
			s.send(sendPacket);
			
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
}