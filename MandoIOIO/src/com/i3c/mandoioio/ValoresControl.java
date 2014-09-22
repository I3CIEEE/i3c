package com.i3c.mandoioio;


public class ValoresControl {
	int veloc = 500;
	int giro = 500;
	
	public int getVeloc() {
		return veloc;
	}
	public void setVeloc(int veloc) {
		if(veloc > 800)
			this.veloc = 800;
		else if(veloc < 200)
			this.veloc = 200;
		else
			this.veloc = veloc;
	}
	
	public int getGiro() {
		return giro;
	}
	public void setGiro(int giro) {
		this.giro = giro;
	}

}
