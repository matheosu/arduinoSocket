package interfaces;

import java.io.IOException;

public interface ILEDClient {

	boolean turnOn() throws IOException;
	
	boolean turnOff() throws IOException;
	
}
