package com.i3c.mandoioio;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class TaskVideo extends AsyncTask<Void, Bitmap, Void> {

	ImageView video;
	DatagramTypeSave dts;
	int nSeq = 0;

	public TaskVideo(ImageView video, DatagramTypeSave dts) {
		this.video = video;
		this.dts = dts;
	}

	@Override
	protected Void doInBackground(Void... params) {
		for (;;) {
			InputStream in = dts.getFotos().leerFoto();
			DataInputStream ins = new DataInputStream(in);
			int nSeq;
			try {
				nSeq = ins.readInt();
				if (nSeq > this.nSeq) {
					System.out.println("foto nº: " + nSeq);
					this.nSeq = nSeq;
					Bitmap bm = BitmapFactory.decodeStream(ins);
					publishProgress(bm);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (isCancelled())
				break;

		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Bitmap... in) {
		video.setImageBitmap(in[0]);
	}
}