package com.i3c.mandoioio;


import java.io.DataInputStream;

public class Fotos {
	private boolean fotoLeida = true;
	private DataInputStream foto;

	public DataInputStream getFoto() {
		return foto;
	}

	public void setFoto(DataInputStream foto) {
		this.foto = foto;
	}

	public synchronized void guardaFoto(DataInputStream foto) {
		this.foto = foto;
		fotoLeida = false;
		notify();
	}

	public synchronized DataInputStream leerFoto() {
		try {
			if (fotoLeida) {
				wait();
			}
			fotoLeida = true;
			return foto;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
