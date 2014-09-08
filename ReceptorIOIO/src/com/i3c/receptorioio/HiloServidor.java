package com.i3c.receptorioio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.widget.TextView;

public class HiloServidor extends Thread {
	DatagramSocket s;
	TextView txtView;
	Client_Properties cP;

	HiloServidor(DatagramSocket s, TextView txtView, Client_Properties cP) {
		this.s = s;
		this.txtView = txtView;
		this.cP = cP;
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
				if(!cP.isSet()){
					cP.setIPAddress(request.getAddress());
					cP.setPort(request.getPort());
					cP.setSet(true);
				}
				Main.cambiarEstado("recibo "+ buffer,txtView);
				Main.mensaje_giro = byteArrayToInt(buffer);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
				| (b[0] & 0xFF) << 24;
	}

}