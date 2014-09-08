package com.i3c.receptorioio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HiloSendStreaming extends Thread {
	byte[] output;
	DatagramSocket s;
	InetAddress addr;
	int port;

	HiloSendStreaming(byte[] out, DatagramSocket s, InetAddress addr, int port) {
		this.output = out;
		this.s = s;
		this.addr = addr;
		this.port = port;
	}

	public void run() {
		try {
			System.out.println(output.length);
			DatagramPacket sendPacket = new DatagramPacket(output, output.length, addr, port);
			s.send(sendPacket);
			System.out.println("envio");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}