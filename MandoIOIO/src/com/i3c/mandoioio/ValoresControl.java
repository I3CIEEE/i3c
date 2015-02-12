package com.i3c.mandoioio;


public class ValoresControl {
	int veloc = 500;
	int giro = 500;
	private float redondeoveloc = 0.4f;
	private float redondeogiro = 0.7f;
	public boolean cambiomarcha = false;
	public boolean cmOnTouch = true;

	
	public int getVeloc() {
		return veloc;
	}
	
	/**
	 * veloc funciona entre 0 y 1000 
	 * 
	 */
	
	public void setVeloc(int veloc) {
		if (! cambiomarcha)
			this.veloc = (int) (veloc / 1000F * 500F * redondeoveloc + 500);
		else
			this.veloc = (int) (Math.abs(veloc / 1000F * 500F  * redondeoveloc - 500));
		System.out.println("veloc -> " + this.veloc);
	}
	
	public int getGiro() {
		return giro;
	}
	
	/**
	 * giro funciona entre 0 y 1000
	 * @param giro
	 */
	
	public void setGiro(int giro) {
		if (giro > 1000)
			this.giro = (int) (1000 * redondeogiro);
		else if (giro < 0)
			this.giro = (int) (1000 - 1000 * redondeogiro);
		else
			this.giro = (int) (giro * redondeogiro + 500 - 1000 / 2 * redondeogiro);
		System.out.println("giro -> " + this.giro);
	}

}
