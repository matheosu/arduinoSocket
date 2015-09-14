package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import interfaces.IArduinoService;
import interfaces.IServer;
import service.ArduinoService;

public class Server implements IServer {

	private ServerSocket serverSocket;
	private IArduinoService arduinoService;
	
	private static final int DEFAULT_PORT = 4000;
	
	public Server() throws IOException {
		openConnection();
		arduinoService = new ArduinoService();
	}

	private void openConnection() throws IOException {
		serverSocket = new ServerSocket(DEFAULT_PORT);
	}

	private void closeConnection() throws IOException {
		serverSocket.close();
		serverSocket = null;
	}
	
	private void process(Socket request) throws IOException{
		System.out.println("Opa" + request.getLocalAddress());
		try {
			InputStream input = request.getInputStream();
			OutputStream output = request.getOutputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			PrintWriter print = new PrintWriter(output, true);
			while (true) {	
				try {
					int read = buffer.read();
					arduinoService.writeData(read);
				} catch (Exception e) {
					print.println("[Arduino Erro]" + e.getMessage());
				}
				break;
			}
			request.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
	@Override
	public void process() throws IOException {
		while(true){
			process(serverSocket.accept());
		}
	}
}
