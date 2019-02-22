package client_package;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import server_package.Command;
import server_package.OutputProcessor;

public class ClientThread extends Thread {
	private Socket client;
	private ConcurrentLinkedQueue<String> clientCom;
	private String clientID;

	public ClientThread(Socket client) throws IOException {
		this.client = client;
		clientCom = new ConcurrentLinkedQueue<String>();
		
	}

	public void run() {
		try (BufferedReader cin = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter cpw = new PrintWriter(client.getOutputStream(), true);) {
			while (true) {
				char temp;
				String msg = "";
				int available = client.getInputStream().available();
				while (cin.ready()) {
					temp = (char) cin.read();
					msg += temp;
					System.out.println("message: " + msg);
					Command c = new Command(this, msg);
					OutputProcessor.addToInputQueue(c);
					msg = "";
					/*
					ArrayList<String> check = RegexParser.matches("(.*) \r\n$", msg);
					if(!check.isEmpty()) {
						System.out.println("Check 1: " + check.get(1));
						Command c = new Command(this, check.get(1));
						OutputProcessor.addToInputQueue(c);
						msg = "";
					}
					*/
				}
				

				if (!clientCom.isEmpty()) {
					byte[] encoded = clientCom.poll().getBytes(Charset.forName("UTF-8"));
					cpw.println(new String(encoded, Charset.forName("UTF-8")));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public synchronized SocketAddress getClientAddress() {
		return client.getRemoteSocketAddress();
	}

	public synchronized Socket getClient() {
		return client;
	}

	public synchronized void talkToClient(String com) {
		clientCom.add(com);
	}
}