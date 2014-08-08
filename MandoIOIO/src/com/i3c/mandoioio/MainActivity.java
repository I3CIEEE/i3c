package com.i3c.mandoioio;

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

public class MainActivity extends Activity implements OnTouchListener,
		SensorEventListener {

	private HiloControl hc;
	private Thread thc;
	private Coordenada coor = new Coordenada();
	private RelativeLayout entrada;
	private int centroX;
	private int centroY;
	private TaskVideo taskVideo = new TaskVideo();
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private TextView azimuthValue;
	private TextView pithValue;
	private TextView rollValue;
	private TextView accurancyValue;

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
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		ImageView iv = (ImageView) findViewById(R.id.videoImage);
		// taskVideo.execute(iv);

		// Para poder utilizar el acelerometro.

		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		// para poder tomar los valores de velocidad y giro al pulsar la
		// pantalla.
		entrada = (RelativeLayout) findViewById(R.id.relative_loyout);
		entrada.setOnTouchListener(this);
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
		if (accelerometer != null) {
			float azimuth = event.values[0];
			float pith = event.values[1];
			float roll = event.values[2];
			azimuthValue.setText(getResources().getString(R.id.azimuth_value)
					+ azimuth);
			pithValue
					.setText(getResources().getString(R.id.pitch_value) + pith);
			rollValue.setText(getResources().getString(R.id.roll_value) + roll);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		 if(sensor != null) {
		 accurancyValue.setText(getResources().getString(R.id.accurancy_value)
		 + accuracy);
		 }

	}

}