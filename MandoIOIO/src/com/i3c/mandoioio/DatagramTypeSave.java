package com.i3c.mandoioio;


public class DatagramTypeSave {
	private Fotos fotos;
	
	public DatagramTypeSave(){
		fotos = new Fotos();
	}
	
	public Fotos getFotos(){
		return fotos;
	}
	
	public void setFotos(Fotos fotos){
		this.fotos = fotos;
	}
}
