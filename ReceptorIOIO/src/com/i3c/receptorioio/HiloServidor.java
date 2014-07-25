package com.i3c.receptorioio;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class HiloServidor extends Thread {
	byte[] output;
	DatagramSocket s;

	HiloServidor(byte[] out, DatagramSocket s) {
		this.output = out;
		this.s = s;
	}

	public void run() {
		int port = 15000;
		try {
			DatagramSocket aSocket = new DatagramSocket(port);
			// Creamos un buffer para entrada paquetes de un fotograma.
			byte[] buffer = new byte[13];
			// Genera un datagrama para mensages de longitud indicada.
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);

			while (true) {
				// Esperamos a recibir algun paquete de un cliente.
				aSocket.receive(request);
				
				InputStream imgStream = new ByteArrayInputStream(buffer);
				DataInputStream s = new DataInputStream (imgStream);
				
				if(s.readByte() == 0){
					
				}
	
				

			}
			
		} catch (Exception e) {
			System.out.println("def");
		}
	}
}