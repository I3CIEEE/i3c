package com.i3c.mandoioio;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
		int nseq = 0;
		for (;;) {
			try {
				Thread.sleep(200);
				int giro = (int) Math.round(valControl.getGiro());
				int veloc = (int) Math.round(valControl.getVeloc());

				byte[] sendData = null;
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				DataOutputStream dout = new DataOutputStream(out);
				
				dout.writeInt(2);
				dout.writeInt(nseq);
				dout.writeInt(giro);
				dout.writeInt(veloc);
				
				sendData = out.toByteArray();
//				System.out.println(sendData.length);
				
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, Ip, port);
				s.send(sendPacket);
				nseq ++;
			} catch (Exception e) {
				e.printStackTrace();
				break;

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
