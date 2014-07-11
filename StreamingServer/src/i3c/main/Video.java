package i3c.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Video extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width = 800;
	private int height = 600;
	private Image imagen;
	private InputStream imgStream;

	public Video() {
		this.setSize(width, height);
		this.setVisible(true);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void cargaImagen(byte[] in) {
		try {
			imgStream = new ByteArrayInputStream(in);
			imagen = ImageIO.read(imgStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();
	}

	@Override
	public void paint(Graphics g) {
		Graphics g2 = (Graphics2D) g;
		g2.drawImage(imagen, 0, 0, width, height, null);
	}

}