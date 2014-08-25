package com.i3c.receptorioio;

import java.net.InetAddress;

public class Client_Properties {
	
	InetAddress IPAddress;
	int port;
	boolean set = false;
	
	public InetAddress getIPAddress() {
		return IPAddress;
	}
	public void setIPAddress(InetAddress iPAddress) {
		IPAddress = iPAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isSet() {
		return set;
	}
	public void setSet(boolean set) {
		this.set = set;
	}
	

}
