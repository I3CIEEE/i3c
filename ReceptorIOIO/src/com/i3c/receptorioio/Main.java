package com.i3c.receptorioio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Stack;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends IOIOActivity {
	
	public static Handler handler = new Handler();
	public static int mensaje_veloc = 550;
	public static int mensaje_giro = 500;
	public static boolean gravando = false;
	public static boolean reproduciendo = false;
	public static Stack<Movimientos> gravacion = new Stack<Movimientos>();
	public static long lagMax = 200;

	
	//Socket
	private Client_Properties ClientProp = new Client_Properties();
	DatagramSocket s;
	int port = 15000;
	static int numSec = 0;
	private ServerSocket ss;
	private Thread hiloVoz;
	
	//Camera
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	private Camera.Size size = null;

    //Settings
    private String compressionPhoto = "20";
    private String typeScreenPreview = "0";
	private boolean configOn = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("aplicacion adri");
		setContentView(R.layout.layout_main);
		RellenaMovimientos rm = new RellenaMovimientos(gravacion);
		rm.rellena();
		
		TextView txtView =(TextView) findViewById(R.id.textView1);
		
		try {
			s = new DatagramSocket(port);
			StartServer(txtView, s, ClientProp);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		HiloOrdenesVoz hov = new HiloOrdenesVoz(getApplicationContext(), ss);
		hiloVoz = new Thread(hov);
		hiloVoz.start();
		
		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BucleIOIO();
	}
	
	@Override
	public void onResume() {
		super.onResume();

		
	}

	@Override
	public void onPause() {

		if (inPreview) {
			camera.stopPreview();
		}
		if(configOn){
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
			inPreview = false;
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hiloVoz.interrupt();
			
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.configMen:
			Intent i = new Intent(this, Settings.class);
			startActivityForResult(i, 2345);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 2345) {
			mostrarPreferencias();		
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			s.close();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    public void mostrarPreferencias() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		compressionPhoto = pref.getString("Compress", "?");
        typeScreenPreview = pref.getString("PrevPhoto", "?");
        camera = Camera.open();
		startPreview();
		cameraConfigured = false;
		configOn=true;
	}

	private Camera.Size getWorstPreviewSize(Camera.Parameters parameters) {
		TextView txtView =(TextView) findViewById(R.id.textView1);
		cambiarEstado("w"+ parameters.getSupportedPreviewSizes().get(Integer.parseInt(typeScreenPreview)).width+"  h"+parameters.getSupportedPreviewSizes().get(Integer.parseInt(typeScreenPreview)).height, txtView);
		//1ºguardo en array, 2º devuelvo la size que toca
		
		return parameters.getSupportedPreviewSizes().get(Integer.parseInt(typeScreenPreview));
		
//		Camera.Size result = null;
//		
//		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
//			if (size.width <= width && size.height <= height) {
//				if (result == null) {
//					result = size;
//				} else {
//					int resultArea = result.width * result.height;
//					int newArea = size.width * size.height;
//
//					if (newArea > resultArea) {
//						result = size;
//					}
//				}
//			}
//		}
//
//		return (result);
	}

	private void initPreview() {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
				camera.setPreviewCallback(callback);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(Main.this, t.getMessage(), Toast.LENGTH_LONG)
						.show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
//				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//				Display display = getWindowManager().getDefaultDisplay(); 
//				int width = display.getWidth();  // deprecated
//				int height = display.getHeight();  // deprecated
//				size = getWorstPreviewSize(width, height, parameters);
				size = getWorstPreviewSize(parameters);
				if (size != null) {
					parameters.setPreviewSize(size.width, size.height);
					camera.setParameters(parameters);
					cameraConfigured = true;
				}
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview = true;
		}
	}

	// Handler para detectar cuando se crea,cambia o se destruye la preview
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			initPreview();
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
	};

	Camera.PreviewCallback callback = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			// Create JPEG

			if (size != null) {
				Rect rectangle = new Rect();
				rectangle.bottom = size.height;
				rectangle.top = 0;
				rectangle.left = 0;
				rectangle.right = size.width;

				YuvImage image = new YuvImage(data, ImageFormat.NV21,
						size.width, size.height, null /* strides */);

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				image.compressToJpeg(rectangle, Integer.parseInt(compressionPhoto), out);
				
				if(ClientProp.isSet()){
					byte[] allToguether;
					
					// Add at the beginning of the arrayImage, the type of msg (photo = 1)
					byte[] numType = ByteBuffer.allocate(4).putInt(1).array();
					// Add the numSec next to the type
					byte[] numSecArray = ByteBuffer.allocate(4).putInt(numSec).array();
					numSec++;
					//everething
					allToguether = concatenateByteArrays(numType, numSecArray);
					allToguether = concatenateByteArrays(allToguether, out.toByteArray());
					
					SendStreaming(allToguether,	ClientProp.getIPAddress(),ClientProp.getPort());
				}
				
			}
		}
	};
	
	byte[] concatenateByteArrays(byte[] first, byte[] second) {
		byte[] result = new byte[second.length + first.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	public void SendStreaming(byte[] out, InetAddress addr, int port) {
		try {
			HiloSendStreaming obj = new HiloSendStreaming(out, s, addr, port);
			(new Thread(obj)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void StartServer(TextView txtView, DatagramSocket s, Client_Properties cP) {
		try {
			HiloServidor obj = new HiloServidor(s, txtView, cP);
			(new Thread(obj)).start();
			//obj.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void cambiarEstado(final String text, final TextView txtView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				txtView.setText(text);
				
			}
		});
	}
}
