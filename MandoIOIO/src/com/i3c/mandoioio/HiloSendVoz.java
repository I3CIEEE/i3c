package com.i3c.mandoioio;

import java.io.DataOutputStream;
import java.net.Socket;

public class HiloSendVoz extends Thread {
	byte[] output;

	HiloSendVoz(byte[] out) {
		this.output = out;
	}

	public void run() {
		try {
			Socket s = new Socket(MainActivity.IPer, 1234);
			
			DataOutputStream dOut = new DataOutputStream(s.getOutputStream());

			dOut.write(output);
			System.out.println("orden envio");
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}