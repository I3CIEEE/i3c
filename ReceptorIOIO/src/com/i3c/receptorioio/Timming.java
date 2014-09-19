package com.i3c.receptorioio;


public class Timming extends Thread {
	
	InterTimming intTim;
	int nseqAntes;
	
	Timming(InterTimming intTim, int nseqAntes) {
		this.intTim = intTim;
		this.nseqAntes= nseqAntes;
	}

	public void run() {
		try {
			sleep(200);
			if(intTim.getNseq()==nseqAntes){
				Main.mensaje_giro = 500;
				Main.mensaje_veloc = 550;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
