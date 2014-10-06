package com.i3c.receptorioio;

import java.util.Stack;

public class RellenaMovimientos {

	Stack<Movimientos> gravacion;
	
	public RellenaMovimientos(Stack<Movimientos> gravacion){
		this.gravacion = gravacion;
	}
	
	public void rellena(){
		gravacion.add(ataca());
		
	}
	
	public Movimientos ataca(){
		Movimiento mov = new Movimiento();
		Movimientos movimientos = new Movimientos();
		Stack<Movimiento> movimiento = movimientos.getMovimientos();
		mov.setGiro(0);
		mov.setTime(100);
		mov.setVeloc(800);
		movimiento.add(0, mov);
		mov = new Movimiento();
		mov.setGiro(0);
		mov.setTime(200);
		mov.setVeloc(500);
		movimiento.add(1, mov);
		mov = new Movimiento();
		mov.setGiro(0);
		mov.setTime(150);
		mov.setVeloc(800);
		movimiento.add(2, mov);
		mov = new Movimiento();
		mov.setGiro(0);
		mov.setTime(150);
		mov.setVeloc(500);
		movimiento.add(3, mov);

		return movimientos;
	}
}
