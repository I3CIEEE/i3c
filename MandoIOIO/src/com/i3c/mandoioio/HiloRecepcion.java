package com.i3c.mandoioio;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class HiloRecepcion implements Runnable {

	private DatagramSocket s;
	private byte[] buffer;
	private DatagramPacket reply;
	private DatagramTypeSave dts;

	public HiloRecepcion(DatagramSocket s, DatagramTypeSave dts) {
		this.s = s;
		this.dts = dts;
		buffer = new byte[70000];
		reply = new DatagramPacket(buffer, buffer.length);
	}
 
	@Override
	public void run() {
		for (;;) {
			try {
				s.receive(reply);
				System.out.println("llega");
				DataInputStream din = new DataInputStream(
						new ByteArrayInputStream(buffer));

				int tipo = din.readInt();

				switch (tipo) {
				case 1:
					Fotos fotos = dts.getFotos();
					fotos.guardaFoto(din);
					break;
				default:
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}
}
