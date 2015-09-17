package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import interfaces.IArduinoService;

public class ArduinoService implements SerialPortEventListener, IArduinoService {

	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
												 "/dev/ttyUSB0", // Linux
												 "COM11", // Windows
												 };
	public static final int TIME_OUT = 2000;
	public static final int DATA_RATE = 9600;
	public static BufferedReader input;
	public static OutputStream output;

	public SerialPort serialPort;
	
	public ArduinoService(){
	}
	
	private void initialize() throws Exception {
		serialPort = (SerialPort) getCommPort().open(this.getClass().getName(), TIME_OUT);
		serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
		output = serialPort.getOutputStream();
		serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
	}

	private CommPortIdentifier getCommPort(){
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) 
					return currPortId;
			}
		}
		throw new NullPointerException("COMM Não identificada");
	}
	
	private synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			serialPort = null;
		}
	}

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				System.out.println(inputLine);
				close();
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	@Override
	public void writeData(int o) throws Exception {
		writeData((byte)o);
	}
	
	public synchronized void writeData(byte data) throws Exception {
		if(serialPort == null) initialize();
		output.write(data);
	}
}
