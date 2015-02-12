package com.i3c.mandoioio;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	private int port = 15000; // Ip y puerto StremingServer
	private String host = "192.168.1.25";

	private HiloControl hc;
	private Thread thc;
	private Thread thc2;
	private ValoresControl valControl = new ValoresControl();
	// private RelativeLayout entrada;
	// private int centroX;
	// private int centroY;
	private TaskVideo taskVideo;
	private DatagramSocket s;
	private InetAddress Ip;
	private HiloRecepcion hr;

	private long last_update = 0;
	private float intY = 0, preY = 0, proY = 0;
	private float P = 0.3f, I = 0.7f;
	private DatagramTypeSave dts;
	
	private ImageView video;
	private TextView fps;
	private SeekBar sbAcelerador;
	private TextView mTvVeloc;
	
	private SpeechRecognizer mSpeechRecognizer;
	private Intent mSpeechRecognizerIntent; 
	private boolean mIslistening;
	public static String IPer;
	public static TextView vozCode;
	public static Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("aplicacion daniel");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Cosas de la seekbar
		mTvVeloc = (TextView) findViewById(R.id.velocidad);
		MySeekBarListener sbPruebaAcelerador = new MySeekBarListener(valControl, mTvVeloc);
		sbAcelerador = (SeekBar) findViewById(R.id.vertical_Seekbar);
		sbAcelerador.setOnSeekBarChangeListener(sbPruebaAcelerador);		
		
		video = (ImageView) findViewById(R.id.videoImage);
		fps = (TextView) findViewById(R.id.nFotoValor);
		vozCode = (TextView) findViewById(R.id.vozCode);

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

		// Para enviar los datos de velocidad y giro al movil.
		hc = new HiloControl(s, valControl, Ip, port);
		thc = new Thread(hc);
		thc.start();

		// Para recibir y mostrar las imagenes recibidas.
		ImageView iv = (ImageView) findViewById(R.id.videoImage);
		dts = new DatagramTypeSave();
		TextView nSeqFoto = (TextView) findViewById(R.id.nFotoValor);
		taskVideo = new TaskVideo(iv, dts, nSeqFoto);
		taskVideo.execute();

		hr = new HiloRecepcion(s, dts);
		thc2 = new Thread(hr);
		thc2.start();

		controlVeloc();
		
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
	    mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
	                                     this.getPackageName());
	    
	    MyRecognitionListener listener = new MyRecognitionListener();
	    mSpeechRecognizer.setRecognitionListener(listener);
	    
	    buttonBehaviour();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.config:
			Intent i = new Intent(this, Settings.class);
			startActivityForResult(i, 2345);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2345) {

			IPer = mostrarPreferencias();

			try {
				s = new DatagramSocket();
				Ip = InetAddress.getByName(IPer);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			thc.interrupt();
			// Para enviar los datos de velocidad y giro al movil.
			hc = new HiloControl(s, valControl, Ip, port);
			thc = new Thread(hc);
			thc.start();

			// Para recibir y mostrar las imagenes recibidas.
			ImageView iv = (ImageView) findViewById(R.id.videoImage);
			dts = new DatagramTypeSave();
			TextView nSeqFoto = (TextView) findViewById(R.id.nFotoValor);
			taskVideo.cancel(true);
			taskVideo = new TaskVideo(iv, dts, nSeqFoto);
			taskVideo.execute();

			thc2.interrupt();
			hr = new HiloRecepcion(s, dts);
			thc2 = new Thread(hr);
			thc2.start();
			
			mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		    mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		                                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
		                                     this.getPackageName());
		    
		    MyRecognitionListener listener = new MyRecognitionListener();
		    mSpeechRecognizer.setRecognitionListener(listener);

		}
	}

	public String mostrarPreferencias() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String s = pref.getString("IpValue", "?");
		return s;
	}

	public void controlVeloc() {
		
		Button bMarcha = (Button) findViewById(R.id.cambiomarcha);
		bMarcha.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				valControl.cambiomarcha = !valControl.cambiomarcha;
				sbAcelerador.setProgress(0);
			}
		});
//		Button bAcelerador = (Button) findViewById(R.id.acelera);
//		bAcelerador.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				TextView tv = (TextView) findViewById(R.id.velocidad);
//				int veloc = valControl.getVeloc() + 1;
//				valControl.setVeloc(veloc);
//				String num = formateaVeloc(valControl.getVeloc() - 500);
//				tv.setText(num);
//				return false;
//			}
//		});
//		Button bDecelerador = (Button) findViewById(R.id.decelera);
//		bDecelerador.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				TextView tv = (TextView) findViewById(R.id.velocidad);
//				int veloc = valControl.getVeloc() - 1;
//				valControl.setVeloc(veloc);
//				String num = formateaVeloc(valControl.getVeloc() - 500);
//				tv.setText(num);
//				return false;
//			}
//		});
//		Button bSacelerador = (Button) findViewById(R.id.superacelera);
//		bSacelerador.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				TextView tv = (TextView) findViewById(R.id.velocidad);
//				int veloc = valControl.getVeloc() + 10;
//				valControl.setVeloc(veloc);
//				String num = formateaVeloc(valControl.getVeloc() - 500);
//				tv.setText(num);
//				return false;
//			}
//		});
//		Button bSdecelerador = (Button) findViewById(R.id.superdecelera);
//		bSdecelerador.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				TextView tv = (TextView) findViewById(R.id.velocidad);
//				int veloc = valControl.getVeloc() - 10;
//				valControl.setVeloc(veloc);
//				String num = formateaVeloc(valControl.getVeloc() - 500);
//				tv.setText(num);
//				return false;
//			}
//		});
//		Button bStop = (Button) findViewById(R.id.stop);
//		bStop.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				TextView tv = (TextView) findViewById(R.id.velocidad);
//				tv.setText("000");
//				valControl.setVeloc(500);
//				return false;
//			}
//		});
//
//	}
		Button bStop = (Button) findViewById(R.id.stop);
		bStop.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mTvVeloc.setText("Frenando!!!");
				if(valControl.cmOnTouch){
					if(valControl.getVeloc() != 500){
						valControl.setVeloc((valControl.getVeloc()/2) + 250);
						mTvVeloc.setText("000");
						valControl.cmOnTouch = true;
					}
				}
				valControl.cmOnTouch = false;
//				if (valControl.getVeloc() != 500 )
//					valControl.setVeloc(1000);
				sbAcelerador.setProgress(0);
				return false;
			}
		});
		
		bStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				mTvVeloc.setText("000");
				valControl.cmOnTouch = true;
				valControl.setVeloc(0);
			}
		});
	}

	private String formateaVeloc(int veloc) {
		Formatter fmt = new Formatter();
		String num;
		if (veloc < 0)
			num = (fmt.format("%04" + "d", veloc)).toString();
		else
			num = (fmt.format("%03d", veloc)).toString();
		fmt.close();
		return num;
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

		long time_difference = current_time - last_update;
		if (time_difference > 0) {
			float Y;
			intY = proY * P + intY * I;
			Y = intY;

			last_update = current_time;
			if (Math.abs(Y - preY) > 0.03) {// Actuador
				TextView pitchTV = (TextView) findViewById(R.id.pitch_value);
				Y = Y * 40;
				preY = Y;
				if ((Math.abs(Y)) > 400)
					Y = Integer.signum(Math.round(Y)) * 400;
				pitchTV.setText(Float.toString(Y));
				ImageView iv = (ImageView) findViewById(R.id.flecha);
				iv.setRotation(45 - Y / 400 * 90);
				valControl.setGiro(Math.round(Y) + 500);
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
		thc.interrupt();
		thc2.interrupt();
		taskVideo.cancel(true);
		if (mSpeechRecognizer != null){
	        mSpeechRecognizer.destroy();
		}
		s.close();
		super.onPause();
	}
	
	public void buttonBehaviour(){
		Button button = (Button) findViewById(R.id.buttonRec);
		button.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					if(!mIslistening){
						mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
						System.out.println("Entra");
						mIslistening = true;
					}
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	mSpeechRecognizer.stopListening();
		        	mIslistening = false;
		        	System.out.println("Sale");
		        }
				return false;
			}
		});
	}
	
	public static void cambiarEstado(final String text) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				vozCode.setText(text);
				
			}
		});
	}

	public class TaskVideo extends AsyncTask<Void, Object, Void> {

		private int nSeq = -1;
		private long timeLast = 0;

		public TaskVideo(ImageView video, DatagramTypeSave dts,
				TextView nSeqFoto) {
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
						System.out.println("foto n�: " + nSeq);
						Bitmap bm = BitmapFactory.decodeStream(ins);
						Object[] outUpdate = new Object[2];
						outUpdate[0] = bm;
						long time = System.currentTimeMillis();
						outUpdate[1] = Math.round(1000/(time - timeLast));
						timeLast = time;
						publishProgress(outUpdate);
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
		protected void onProgressUpdate(Object... in) {
			video.setImageBitmap((Bitmap)in[0]);
			fps.setText(Integer.toString((Integer)in[1]));
		}
	}
	
	
}
