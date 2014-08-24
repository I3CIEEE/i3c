package com.i3c.mandoioio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class TaskVideo extends AsyncTask<Void, Bitmap, Void>{

	ImageView video;
	DatagramSocket s;
	byte[] buffer;
	DatagramPacket reply;
	
	public TaskVideo(ImageView video, DatagramSocket s){
		this.video = video;
		this.s = s;
        buffer = new byte[15000];
        reply = new DatagramPacket(buffer, buffer.length);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		try {
			for(;;){
				s.receive(reply);
				System.out.println("llega");
				InputStream in = new ByteArrayInputStream(buffer);
				Bitmap bm = BitmapFactory.decodeStream(in);
				System.out.println(bm);
				publishProgress(bm);
				if(isCancelled())
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
     @Override
     protected void onProgressUpdate(Bitmap... in) {
    	 video.setImageBitmap(in[0]);
     }
}