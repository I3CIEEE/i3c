package com.i3c;

import java.io.ByteArrayOutputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class Main extends IOIOActivity {

	public static int mensaje_veloc = 550;
	public static int mensaje_giro = 500;

	// Variables camara
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	private Camera.Size size = null;
	DatagramSocket s;
	static int numSec = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.surface_view);

		try {
			s = new DatagramSocket();
		} catch (SocketException e) {

			e.printStackTrace();
		}

		preview = (SurfaceView) findViewById(R.id.preview);
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		// previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BucleIOIO();
	}

	@Override
	public void onResume() {
		super.onResume();

		camera = Camera.open();
		startPreview();
	}

	@Override
	public void onPause() {

		if (inPreview) {
			camera.stopPreview();
		}

		camera.setPreviewCallback(null);
		camera.release();
		camera = null;
		inPreview = false;
		s.close();
		super.onPause();
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

	private void initPreview(int width, int height) {
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
				size = getBestPreviewSize(width, height, parameters);

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
			initPreview(width, height);
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
				image.compressToJpeg(rectangle, 71, out);

				// Add at the end of the array of image the numSec
				byte[] numSecArray = ByteBuffer.allocate(4).putInt(numSec).array();
				numSec++;
				Send(concatenateByteArrays(out.toByteArray(),numSecArray));
				//Send(data);
			}
		}
	};

	byte[] concatenateByteArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	public void Send(byte[] out) {
		try {
			SendStreamingServer obj = new SendStreamingServer(out, s);
			obj.start();
			obj.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}