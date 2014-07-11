package com.i3c;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class BucleIOIO extends BaseIOIOLooper{
	
	private PwmOutput[] servos;
	public static boolean conectado = false;

	@Override
	protected void setup() throws ConnectionLostException {
		servos = new PwmOutput[3];
		servos[0] = ioio_.openPwmOutput(4, 50); //3 y 50Hz Velocidad
		servos[1] = ioio_.openPwmOutput(13, 50); // Servo
		conectado = true;
	}

	@Override
	public void disconnected() {
		//MainActivity.cambiarEstado("Desconectado IOIO");
	}

	@Override
	public void loop() throws ConnectionLostException {
		try {
			servos[0].setPulseWidth(1000 + Main.mensaje_veloc);
			servos[1].setPulseWidth(1000 + Main.mensaje_giro);
			Thread.sleep(100);
			
		} catch (InterruptedException e) {
		}
	}
}