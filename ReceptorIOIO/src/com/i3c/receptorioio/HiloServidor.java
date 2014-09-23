package com.i3c.receptorioio;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Stack;

import android.widget.TextView;

public class HiloServidor extends Thread {
	DatagramSocket s;
	TextView txtView;
	Client_Properties cP;
	int nSeq;
	boolean hasCome;
	InterTimming intTim;
	boolean gravando = false;
	Movimientos movimientos = null;

	HiloServidor(DatagramSocket s, TextView txtView, Client_Properties cP) {
		this.s = s;
		this.txtView = txtView;
		this.cP = cP;
		this.hasCome = false;
		this.nSeq = -1;
		this.intTim = new InterTimming();
	}

	public void run() {
		try {

			byte[] buffer = new byte[16];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			Main.cambiarEstado("Server started, waiting..", txtView);
			while (true) {

				s.receive(request);
				if (!cP.isSet()) {
					cP.setIPAddress(request.getAddress());
					cP.setPort(request.getPort());
					cP.setSet(true);
				}
				Main.cambiarEstado("recibo ", txtView);

				ByteArrayInputStream bos = new ByteArrayInputStream(buffer);
				DataInputStream dtI = new DataInputStream(bos);

				int type = dtI.readInt();

				if (type == 2) {
					int nseqRecv = dtI.readInt();

					intTim.setNseq(nseqRecv);
					Timming obj = new Timming(intTim, nseqRecv);
					(new Thread(obj)).start();

					if (nseqRecv > nSeq) {
						nSeq = nseqRecv;
						SetsGyV(dtI, gravando);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void SetsGyV(DataInputStream dtI, boolean gravando) {
		try {
			if(!Main.reproduciendo){
			Main.mensaje_giro = dtI.readInt();
			Main.mensaje_veloc = dtI.readInt();
			}
			if (Main.gravando) {
				if (movimientos == null)
					movimientos = new Movimientos();
				Movimiento movimiento = new Movimiento();
				movimiento.setGiro(Main.mensaje_giro);
				movimiento.setTime(System.currentTimeMillis());
				if (!movimientos.getMovimientos().isEmpty()) {
					Movimiento ultimo = movimientos.getMovimientos()
							.lastElement();
					ultimo.setTime(movimiento.getTime() - ultimo.getTime());
				}
				movimientos.getMovimientos().add(movimiento);
			} else {
				if (movimientos != null)
					Main.gravacion.add(movimientos);
			}
			System.out.println("traza: giro: " + Main.mensaje_giro);
			System.out.println("traza: veloc: " + Main.mensaje_veloc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
				| (b[0] & 0xFF) << 24;
	}

}