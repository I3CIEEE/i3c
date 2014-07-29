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
			String host = "192.168.0.149";
			
			InetAddress Ip = InetAddress.getByName(host);
		
			
			//byte[] sendData = new byte[1024];
		     // byte[] receiveData = new byte[1024];
		    //String sentence = "hola";
		    //sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(output,
					output.length, Ip, port);
			s.send(sendPacket);
			//System.out.println("me cago en to");
		} catch (Exception e) {
			
			e.printStackTrace();

		}

	}
}