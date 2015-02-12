package com.i3c.mandoioio;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MySeekBarListener implements OnSeekBarChangeListener {

	private ValoresControl valControl;
	private TextView tveloc;
	
	public MySeekBarListener(ValoresControl valControl, TextView tveloc) {
		this.valControl = valControl;
		this.tveloc = tveloc;
	}
	

	@Override
	public void onProgressChanged(SeekBar arg0, int veloc, boolean arg2) {
		valControl.setVeloc(veloc * 10); //Se multiplica por que tiene que ir de 0 a 1000
		tveloc.setText(Integer.toString(veloc));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

}
