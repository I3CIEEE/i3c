package com.i3c.mandoioio;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	int port = 15000; // Ip y puerto StremingServer
	String host = "192.168.1.132";

	private HiloControl hc;
	private Thread thc;
	private ValoresControl valControl = new ValoresControl();
	// private RelativeLayout entrada;
	// private int centroX;
	// private int centroY;
	private TaskVideo taskVideo;
	DatagramSocket s;
	InetAddress Ip;

	private long last_update = 0;
	private float intY = 0, intZ = 0;
	private float proY = 0, proZ = 0;
	private float preY = 0, preZ = 0;
	private float P = (float) 0.3;
	private float I = (float) 0.7;
	private TextView pitchTV;
	private TextView rollTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			s = new DatagramSocket();
			Ip = InetAddress.getByName(host);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("conexion establecida");

		// Para enviar los datos de velocidad y giro al movil.
		hc = new HiloControl(s, valControl, Ip, port);
		thc = new Thread(hc);
		thc.start();

		// Para recibir y mostrar las imagenes recibidas.
		ImageView iv = (ImageView) findViewById(R.id.videoImage);
		taskVideo = new TaskVideo(iv, s);

		taskVideo.execute();

		pitchTV = (TextView) findViewById(R.id.pitch_value);
		rollTV = (TextView) findViewById(R.id.roll_value);

		controlVeloc();
	}

	public void controlVeloc() {
		Button bAcelerador = (Button) findViewById(R.id.acelera);
		bAcelerador.setOnTouchListener(new OnTouchListener() {
			private Formatter fmt;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				fmt = new Formatter();
				TextView tv = (TextView) findViewById(R.id.velocidad);
				int veloc = Integer.valueOf((String) tv.getText()) + 1;
				String num;
				if(veloc < 0)
					num = (fmt.format("%04" + "d", veloc)).toString();
				else
					num = (fmt.format("%03d", veloc)).toString();
				tv.setText(num);
				valControl.setVeloc(veloc);
				return false;
			}
		});
		Button bDecelerador = (Button) findViewById(R.id.decelera);
		bDecelerador.setOnTouchListener(new OnTouchListener() {
			private Formatter fmt;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				fmt = new Formatter();
				TextView tv = (TextView) findViewById(R.id.velocidad);
				int veloc = Integer.valueOf((String) tv.getText()) - 1;
				String num;
				if(veloc < 0)
					num = (fmt.format("%04" + "d", veloc)).toString();
				else
					num = (fmt.format("%03d", veloc)).toString();
				tv.setText(num);
				valControl.setVeloc(veloc);
				return false;
			}
		});
	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// if (MotionEvent.ACTION_UP == event.getAction()) {
	// System.err.println("despuseado");
	// coor.setPush(false);
	// } else if (MotionEvent.ACTION_DOWN == event.getAction()) {
	// System.err.println("puseado");
	// if (centroX == 0) {
	// centroY = entrada.getHeight() / 2;
	// centroX = entrada.getWidth() / 2;
	// }
	// coor.setX(event.getX() - centroX);
	// coor.setY(event.getY() - centroY);
	// coor.setPush(true);
	// } else {
	// coor.setX(event.getX() - centroX);
	// coor.setY(event.getY() - centroY);
	// }
	// return true;
	// }

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

		long current_time = event.timestamp;

		proY = event.values[1];
		proZ = event.values[2];

		long time_difference = current_time - last_update;
		if (time_difference > 0) {
			float Y;
			float Z;
			intY = proY * P + intY * I;
			intZ = proZ * P + intZ * I;
			Y = intY;
			Z = intZ;

			last_update = current_time;
			if (Math.abs(Y - preY + Z - preZ) > 0.1) {
				// pitchTV.setText(Float.toString(Y * 90));
				// rollTV.setText(Float.toString(Z * 90));
				preY = Y;
				preZ = Z;

				valControl.setGiro(Y);
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	protected void onResume() {
		super.onResume();
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sm.registerListener(this, sensors.get(0),
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	@Override
	protected void onPause() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.unregisterListener(this);
		super.onPause();
	}

	public void stop(View v) {
		TextView tv = (TextView) findViewById(R.id.velocidad);
		tv.setText("000");
		valControl.setVeloc(500);
	}

}