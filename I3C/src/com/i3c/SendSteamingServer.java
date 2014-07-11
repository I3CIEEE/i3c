package com.i3c;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendSteamingServer extends Thread{
	byte[] output;

	SendSteamingServer(byte[] out) {
		this.output = out;
	}

	public void run() {
		System.out.println(output.length);
		try {
			int port = 14000; //Ip y puerto StremingServer
			String host = "193.147.73.208";
			
			DatagramSocket s = new DatagramSocket(port,
					InetAddress.getByName(host));
			
			DatagramPacket pack = new DatagramPacket(output, output.length);
			
			s.send(pack);

			s.close();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}
}
