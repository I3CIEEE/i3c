package com.i3c.receptorioio;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class BucleIOIO extends BaseIOIOLooper{

	private PwmOutput servo;
	private PwmOutput variador;
	private DigitalOutput led;
	private DigitalOutput ojoizquierdo;
	private DigitalOutput ojoderecho;
	

	@Override
	protected void setup() throws ConnectionLostException {
		led = ioio_.openDigitalOutput(0, true);
		servo = ioio_.openPwmOutput(4, 50); //3 y 50Hz Velocidad
		variador = ioio_.openPwmOutput(13, 50); // Servo
	}

	@Override
	public void loop() throws ConnectionLostException {
		led.write(false);
		try {
			servo.setPulseWidth(1000 + Main.mensaje_veloc);
			variador.setPulseWidth(1000 + Main.mensaje_giro);
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
