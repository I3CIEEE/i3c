package i3c.main;

public class BufferImage {
	private int longImage = 15000;
	private byte[] image = null;
	private int longBuffer = Integer.SIZE + longImage;
	private byte[] buffer = new byte[longBuffer];
	
	public int getnSec() {
		return buffer[0] << 24 | (buffer[1] & 0xFF) << 16 | (buffer[2] & 0xFF) << 8 | (buffer[3] & 0xFF);
	}
	
	public byte[] getImage() {
		if(image == null){
			image = new byte[longImage];
		}
		System.arraycopy(buffer, Integer.SIZE, image, 0, longImage);
		return image;
	}
	
	public void setImage(byte[] image) {
		this.image = image;
	}
	
	public int getLongImage(){
		return longImage;
	}
	
	public int getLongBuffer(){
		return longBuffer;
	}
	
	public byte[] getBuffer() {
		return buffer;
	}
	
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
}
