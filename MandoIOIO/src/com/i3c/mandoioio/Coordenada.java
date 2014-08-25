package com.i3c.mandoioio;


public class Coordenada {
	private float x;
	private float y;
	private boolean push = false;

	public synchronized void setX(float x){
		this.x = x;
	}
	public synchronized void setY(float y){
		this.y = y;
	}
	public synchronized float getX(){
		return x;
	}
	public synchronized float getY(){
		return y;
	}
	public synchronized boolean isPush() {
		return push;
	}
	public synchronized void setPush(boolean push) {
		this.push = push;
	}
}
