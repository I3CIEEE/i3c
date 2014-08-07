package com.i3c.mandoioio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnTouchListener {

	private HiloControl hc;
	private Thread thc;
	private Coordenada coor = new Coordenada();
	private LinearLayout entrada;
	private int centroX;
	private int centroY;
	private TaskVideo tv = new TaskVideo();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		hc = new HiloControl(coor);
		thc = new Thread(hc);
		thc.start();
		entrada = (LinearLayout) findViewById(R.id.layout);
		ImageView iv = (ImageView) findViewById(R.id.videoImage);
		tv.execute(iv);
		
		entrada.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(MotionEvent.ACTION_UP== event.getAction()){
			System.err.println("despuseado");
			coor.setPush(false);
		}else if(MotionEvent.ACTION_DOWN== event.getAction()){
			System.err.println("puseado");
			if(centroX == 0){
				centroY = entrada.getHeight()/2;
				centroX = entrada.getWidth()/2;
			}
			coor.setX(event.getX() - centroX);
			coor.setY(event.getY() - centroY);
			coor.setPush(true);
		}else{
			coor.setX(event.getX() - centroX);
			coor.setY(event.getY() - centroY);
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			thc.interrupt();
			tv.cancel(false);
		}
		return super.onKeyDown(keyCode, event);
	}
	
}