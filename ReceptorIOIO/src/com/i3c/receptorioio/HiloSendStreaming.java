package com.i3c.receptorioio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class HiloSendStreaming extends Thread {
	byte[] output;
	DatagramSocket s;

	HiloSendStreaming(byte[] out, DatagramSocket s) {
		this.output = out;
		this.s = s;
	}

	public void run() {
		try {
			DatagramPacket sendPacket = new DatagramPacket(output, output.length);
			s.send(sendPacket);
			System.out.println("me cago en to");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}