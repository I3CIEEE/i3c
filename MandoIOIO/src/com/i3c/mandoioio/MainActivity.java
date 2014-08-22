package com.i3c.mandoioio;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener,
		SensorEventListener {

	private HiloControl hc;
	private Thread thc;
	private Coordenada coor = new Coordenada();
	private RelativeLayout entrada;
	private int centroX;
	private int centroY;
	private TaskVideo taskVideo = new TaskVideo();
	
	private long last_update = 0, last_movement = 0;
	private float prevX = 0, prevY = 0, prevZ = 0;
	private float curX = 0, curY = 0, curZ = 0;

	public MainActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Para enviar los datos de velocidad y giro al movil.
		// hc = new HiloControl(coor);
		// thc = new Thread(hc);
		// thc.start();

		// Para recibir y mostrar las imagenes recibidas.
		ImageView iv = (ImageView) findViewById(R.id.videoImage);
		// taskVideo.execute(iv);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction()) {
			System.err.println("despuseado");
			coor.setPush(false);
		} else if (MotionEvent.ACTION_DOWN == event.getAction()) {
			System.err.println("puseado");
			if (centroX == 0) {
				centroY = entrada.getHeight() / 2;
				centroX = entrada.getWidth() / 2;
			}
			coor.setX(event.getX() - centroX);
			coor.setY(event.getY() - centroY);
			coor.setPush(true);
		} else {
			coor.setX(event.getX() - centroX);
			coor.setY(event.getY() - centroY);
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			thc.interrupt();
			taskVideo.cancel(false);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		synchronized (this) {
	        long current_time = event.timestamp;
	        
	        curY = event.values[1];
	        curZ = event.values[2];
	         
	        if (prevX == 0 && prevY == 0 && prevZ == 0) {
	            last_update = current_time;
	            last_movement = current_time;
	            prevX = curX;
	            prevY = curY;
	            prevZ = curZ;
	        }
	 
	        long time_difference = current_time - last_update;
	        if (time_difference > 0) {
	            float movement = Math.abs((curX + curY + curZ) - (prevX - prevY - prevZ)) / time_difference;
	            int limit = 1500;
	            float min_movement = 1E-6f;
	            if (movement > min_movement) {
	                if (current_time - last_movement >= limit) {                    
	                    Toast.makeText(getApplicationContext(), "Hay movimiento de " + movement, Toast.LENGTH_SHORT).show();
	                }
	                last_movement = current_time;
	            }
	            prevX = curX;
	            prevY = curY;
	            prevZ = curZ;
	            last_update = current_time;
	        }
	         
	         
	        ((TextView) findViewById(R.id.pitch_text)).setText("Acelerómetro Y: " + curY);
	        ((TextView) findViewById(R.id.roll_text)).setText("Acelerómetro Z: " + curZ);
	    }   
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	    List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);       
	    if (sensors.size() > 0) {
	        sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
	    }
	}
	
	@Override
	protected void onPause() {
	    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);       
	    sm.unregisterListener(this);
	    super.onPause();
	}

}