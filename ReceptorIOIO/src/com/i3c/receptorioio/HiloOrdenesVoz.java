package com.i3c.receptorioio;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

import android.content.Context;
import android.media.MediaPlayer;

public class HiloOrdenesVoz extends Thread {

	private Context context;
	private MediaPlayer sonido;
	private ServerSocket ss;

	public HiloOrdenesVoz(Context context, ServerSocket ss) {
		this.context = context;
		this.ss = ss;
	}

	@Override
	public void run() {
		try {
			ss = new ServerSocket(1234);
			for (;;) {
				Socket s = ss.accept();


				DataInputStream in = new DataInputStream(s.getInputStream());

				int tipo = in.readInt();
				System.out.println("tipo = " + tipo);
				switch (tipo) {
				case 3:
					Main.reproduciendo = true;
					runOrden(in);
					Main.reproduciendo = false;
					break;
				case 4:
					if(in.readInt() == 1)
						Main.gravando = true;
					else
						Main.gravando = false;
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runOrden(DataInputStream in) throws IOException {
		Stack<Movimiento> movimientos;
		int orden = in.readInt();
		System.out.println("orden = " + orden);
		switch (orden) {
		case 1:
			// serhii samu
			sonido = MediaPlayer.create(context, R.raw.cerdo);
			sonido.start();
			break;
		case 2:
			// ataca
			sonido = MediaPlayer.create(context, R.raw.perroleon);
			sonido.start();
			movimientos = Main.gravacion.get(0).getMovimientos();
			mueve(movimientos);
			break;
		case 3:
			// carrera
			movimientos = Main.gravacion.get(1).getMovimientos();
			mueve(movimientos);
			break;
		case 4:
			// voltereta
			movimientos = Main.gravacion.get(4).getMovimientos();
			mueve(movimientos);
			break;
		case 5:
			// busca
			movimientos = Main.gravacion.get(2).getMovimientos();
			mueve(movimientos);
			break;
		case 6:
			// victoria
			sonido = MediaPlayer.create(context, R.raw.victoria);
			sonido.start();
			break;
		case 7:
			// caballo
			sonido = MediaPlayer.create(context, R.raw.caballo);
			sonido.start();
			break;
		case 8:
			// vaca
			sonido = MediaPlayer.create(context, R.raw.vaca);
			sonido.start();
			break;
		case 9:
			// baila
			movimientos = Main.gravacion.get(3).getMovimientos();
			mueve(movimientos);
			break;
		default:
			break;
		}

	}

	private void mueve(Stack<Movimiento> movimientos) {
		for (Movimiento movimiento : movimientos) {
			Main.mensaje_giro = movimiento.getGiro();
			System.out.println("giro " + movimiento.getGiro());
			Main.mensaje_veloc = movimiento.getVeloc();
			System.out.println("veloc " + movimiento.getVeloc());
			if (movimiento.getTime() < Main.lagMax + 100)
				System.out.println("time " + movimiento.getTime());
				try {
					Thread.sleep(movimiento.getTime());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
