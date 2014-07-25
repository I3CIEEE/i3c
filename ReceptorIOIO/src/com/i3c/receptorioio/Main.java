package com.i3c.receptorioio;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;

public class Main extends IOIOActivity {
	
	public static int mensaje_veloc = 550;
	public static int mensaje_giro = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BucleIOIO();
	}
}
