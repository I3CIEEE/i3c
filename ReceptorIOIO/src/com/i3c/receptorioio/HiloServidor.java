package com.i3c.receptorioio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class HiloServidor extends Thread {
	DatagramSocket s;

	HiloServidor(DatagramSocket s) {
		this.s = s;
	}

	public void run() {
		int port = 15000;
		try {
			s = new DatagramSocket(port);
			// Creamos un buffer para entrada paquetes de un fotograma.
			byte[] buffer = new byte[13];
			// Genera un datagrama para mensages de longitud indicada.
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			System.out.println("Esperando");
			while (true) {
				// Esperamos a recibir algun paquete de un cliente.
				s.receive(request);
				System.out.println("recibo");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}