package com.i3c.receptorioio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.widget.TextView;

public class HiloServidor extends Thread {
	DatagramSocket s;
	TextView txtView;

	HiloServidor(DatagramSocket s,TextView txtView) {
		this.s = s;
		this.txtView = txtView;
	}

	public void run() {
		try {
			// Creamos un buffer para entrada paquetes de un fotograma.
			byte[] buffer = new byte[13];
			// Genera un datagrama para mensages de longitud indicada.
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			Main.cambiarEstado("Server started, waiting..", txtView);
			//Main.cambiarEstado("Esperando conexion",txtView);
			while (true) {
				// Esperamos a recibir algun paquete de un cliente.
				s.receive(request);
				//Main.cambiarEstado("recibo "+ buffer,txtView);
				System.out.println("recibo "+ buffer);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}