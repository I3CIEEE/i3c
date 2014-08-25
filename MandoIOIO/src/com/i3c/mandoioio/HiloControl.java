package com.i3c.mandoioio;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HiloControl implements Runnable {

	DatagramSocket s;
	ValoresControl valControl;
	InetAddress Ip;
	int port;

	public HiloControl(DatagramSocket s, ValoresControl valControl,
			InetAddress Ip, int port) {
		this.s = s;
		this.valControl = valControl;
		this.Ip = Ip;
		this.port = port;
	}

	@Override
	public void run() {
		for (;;) {
			try {
				Thread.sleep(20);
				int giro = (int) Math.round(valControl.getGiro());

				byte[] sendData = intToByteArray(giro);
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, Ip, port);

				s.send(sendPacket);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	public static byte[] intToByteArray(int a) {
		byte[] ret = new byte[4];
		ret[3] = (byte) (a & 0xFF);
		ret[2] = (byte) ((a >> 8) & 0xFF);
		ret[1] = (byte) ((a >> 16) & 0xFF);
		ret[0] = (byte) ((a >> 24) & 0xFF);
		return ret;
	}
}
