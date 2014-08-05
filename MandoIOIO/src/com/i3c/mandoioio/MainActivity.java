package com.i3c.mandoioio;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnTouchListener {

	private HiloControl hc;
	private Thread thc;
	private Coordenada coor = new Coordenada();
	private LinearLayout entrada;
	private int centroX;
	private int centroY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		hc = new HiloControl(coor);
		thc = new Thread(hc);
		thc.start();
		entrada = (LinearLayout) findViewById(R.id.layout);
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
		}
		return super.onKeyDown(keyCode, event);
	}
	
}