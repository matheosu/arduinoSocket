package controller;

import java.io.IOException;

public class MainClient {

	private static final int DEFAULT_PORT = 4000;
	
	public static void main(String[] args) throws IOException {
		LEDClient led = new LEDClient(DEFAULT_PORT);
		led.turnOn();
	}

}
