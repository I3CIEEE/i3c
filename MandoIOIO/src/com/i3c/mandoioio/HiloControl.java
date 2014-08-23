package com.i3c.mandoioio;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.view.View.OnTouchListener;

public class HiloControl implements Runnable{
	
	private Coordenada coor;
	
	public HiloControl(Coordenada coor){
		this.coor = coor;
	}

	@Override
	public void run() {
		try {
			
			int port = 15000; //Ip y puerto StremingServer
			String host = "192.168.43.36";
			
			DatagramSocket s = new DatagramSocket();
			InetAddress Ip = InetAddress.getByName(host);
			for(;;){
				if(coor.isPush()){
					System.out.println("X: " + coor.getX());
					System.out.println("Y: " + coor.getY());
				}
				Thread.sleep(20); 
//				
//				String sentence = Integer.toString(i);
//				byte[] sendData = sentence.getBytes();
//				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, Ip, port); 
//			
//				s.send(sendPacket);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
