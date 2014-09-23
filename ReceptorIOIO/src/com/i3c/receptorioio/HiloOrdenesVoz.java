package com.i3c.receptorioio;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

public class HiloOrdenesVoz extends Thread {

	@Override
	public void run() {
		for (;;) {
			try {
				ServerSocket ss = new ServerSocket(15001);
				Socket s = ss.accept();

				DataInputStream in = new DataInputStream(s.getInputStream());

				int tipo = in.readInt();
				switch (tipo) {
				case 1:
					Main.reproduciendo = true;
					runOrden(in);
					break;
				case 2:
					Main.gravando = true;
					break;
				default:
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void runOrden(DataInputStream in) {
		try {
			int orden = in.readInt();
			Stack<Movimiento> movimientos = Main.gravacion.get(orden)
					.getMovimientos();
			for (Movimiento movimiento : movimientos) {
				Main.mensaje_giro = movimiento.getGiro();
				Main.mensaje_veloc = movimiento.getVeloc();
				if (movimiento.getTime() < Main.lagMax+100)
					try {
						Thread.sleep(movimiento.getTime());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
