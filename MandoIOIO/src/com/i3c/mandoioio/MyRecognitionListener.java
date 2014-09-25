package com.i3c.mandoioio;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

class MyRecognitionListener implements RecognitionListener {

	@Override
	public void onBeginningOfSpeech() {
		System.out.println("onBeginningOfSpeech");
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		System.out.println("onBufferReceived");
	}

	@Override
	public void onEndOfSpeech() {
		System.out.println("onEndOfSpeech");
	}

	@Override
	public void onError(int error) {
		System.out.println("onError " + error);
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		System.out.println("onEvent");
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		System.out.println("onPartialResults");
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		System.out.println("onReadyForSpeech");
	}

	@Override
	public void onResults(Bundle results) {
		System.out.println("onResults");
		ArrayList<String> strlist = results
				.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		int orden=0;
		for (int i = 0; i < strlist.size(); i++) {
			orden = checkWord(strlist.get(i));
			if (orden>0){
				break;
			}
		}
		
		int type=0;
		if(orden>2){
			orden = orden -2; //para el protocolo
			type = 3;
		}else if(orden>0){
			type = 4;
		}
		System.out.println("orden "+orden);
		System.out.println("orden t"+type);
		//MainActivity.cambiarEstado(Integer.toString(orden));
		//Add tipo
		if(orden>0){
			
			byte[] sendData = null;
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(out);
			
			try {
				dout.writeInt(type);
				dout.writeInt(orden);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			sendData = out.toByteArray();
			
			
			HiloSendVoz obj = new HiloSendVoz(sendData);
			(new Thread(obj)).start();
		}
	}
	
	byte[] concatenateByteArrays(byte[] first, byte[] second) {
		byte[] result = new byte[second.length + first.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		System.out.println("onRmsChanged");
	}
	
	private enum Words {
		aprende, terminar,Sergi,Samuel,ataca,carrera,voltereta,busca,victoria,caballo,vaca,baila, NOVALUE;
				
		public static Words toExt(String str)
	    {
	        try {
	            return valueOf(str);
	        }
	        catch (IllegalArgumentException ex) {
	            return NOVALUE;
	        }
	    } 
	}
	
	public int checkWord (String p){
		
		switch(Words.toExt(p)){
			case aprende:
				return 1;
			case terminar:
				return 2;
			case Sergi:
				return 3;
			case Samuel:
				return 3;
			case ataca:
				return 4;
			case carrera:
				return 5;
			case voltereta:
				return 6;
			case busca:
				return 7;
			case victoria:
				return 8;
			case caballo:
				return 9;
			case vaca:
				return 10;
			case baila:
				return 11;
	        default: 
	        	return 0;
	    
		}
	}

}
