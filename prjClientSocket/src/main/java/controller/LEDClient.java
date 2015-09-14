package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import interfaces.ILEDClient;

public class LEDClient implements ILEDClient {

	private Socket socket;
	private InputStream input;
	private OutputStream output;
	private PrintWriter writer;
	private BufferedReader reader;

	private int port;

	public LEDClient(int port) throws IOException {
		this.port = port;
		openConnection();
	}

	private void openConnection() throws IOException {
		socket = new Socket(InetAddress.getLocalHost(), port);
		input = socket.getInputStream();
		output = socket.getOutputStream();
		writer = new PrintWriter(output, true);
		reader = new BufferedReader(new InputStreamReader(input));
	}

	private void closeConnection() throws IOException {
		reader.close();
		reader = null;
		writer.close();
		writer = null;
		socket.shutdownInput();
		socket.shutdownOutput();
		socket.close();
		socket = null;
	}

	private boolean isSocketClosed() {
		return this.socket != null && this.socket.isClosed();
	}

	@Override
	public boolean turnOn() throws IOException {
		if (isSocketClosed())
			openConnection();
		return onLED();
	}

	private boolean onLED() throws IOException {
		final int on = 1;
		this.writer.print(on);
		System.out.println("Ligando LED...");
		try {
			while (true) {
				int response = this.reader.read();
				if (response == on) {
					System.out.println("LED Ligada!");
					return true;
				}
			}
		} catch (IOException e) {
			System.err.println("[Erro ao tentar ligar a LED]: " + e.getMessage());
			return false;
		} finally {
			closeConnection();
		}
	}

	@Override
	public boolean turnOff() throws IOException {
		if(isSocketClosed())
			openConnection();
		return offLED();
	}

	private boolean offLED() throws IOException {
		final int off = 0;
		this.writer.print(off);
		System.out.println("Desligando a LED...");
		try {
			while (true) {
				int response = this.reader.read();
				if (response == off) {
					System.out.println("LED Desligada!");
					return true;
				}
			}
		} catch (IOException e) {
			System.err.println("[Erro ao tentar desligar a LED]: " + e.getMessage());
			return false;
		} finally {
			closeConnection();
		}
	}

}
