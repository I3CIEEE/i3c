package i3c.main;

import java.applet.Applet;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Applet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DatagramSocket aSocket;
	private BufferImage bi = new BufferImage();

	private static int usage(String[] args, int port) {
		if (args.length == 1) {
			try {
				return Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("No has introducido un numero valido");
				System.exit(1);
			}
		}
		return port;
	}
	
	public void recibeVideo(Video v){
		int nsec = -1;
		try {
			
			DatagramPacket request = new DatagramPacket(bi.getBuffer(), bi.getLongBuffer());
		
			while (true) {
				// Esperamos a recibir algun paquete de un cliente.
				aSocket.receive(request);
				if(bi.getnSec() > nsec){
					if(bi.getnSec() > nsec + 1){
						for (int i = nsec; i < bi.getnSec(); i++) {
							System.err.println("Saltamos el fotograma numero: " + i);
						}
					}
					nsec = bi.getnSec();							
					v.cargaImagen(bi.getImage());
				}else{
					System.err.println("El fotograma numero: " + bi.getnSec() + "ha llegado tarde");
				}
			}

		} catch (SocketException ex) {

			Logger.getLogger(Server.class.getName())
					.log(Level.SEVERE, null, ex);

		} catch (IOException ex) {

			Logger.getLogger(Server.class.getName())
					.log(Level.SEVERE, null, ex);
		}

	}

	public void start(int port) {
		Video v = new Video();
		
		// Crea un socket para datagramas y le asocia al puerto.
		try {
			aSocket = new DatagramSocket(port);
			recibeVideo(v);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port = 14000;

		port = usage(args, port);
		
		Server s = new Server();
		s.start(port);
	}

}
