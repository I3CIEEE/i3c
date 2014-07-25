package asd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class asda {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			int port = 14000; //Ip y puerto StremingServer
			String host = "192.168.0.149";
			
			DatagramSocket s = new DatagramSocket();
			InetAddress Ip = InetAddress.getByName(host);
			byte[] sendData = new byte[1024];
			String sentence = "hola";
		    sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, Ip, port); 
			
			s.send(sendPacket);
			//DatagramPacket pack = new DatagramPacket(output, output.length);
			
			//s.send(pack);
			System.out.println("verga");
			s.close();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
