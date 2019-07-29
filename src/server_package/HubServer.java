package server_package;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ServerSocketFactory;

import client_package.ClientThread;

public class HubServer extends Thread {

	private ServerSocket server;
	//List of connected Clients
	private ArrayList<ClientThread> connectedClients;
	//
	private BlockingQueue<String> comClients;
	//Private class that waits for clients to connect
	private ClientCollector collector;
	private BlockingQueue<String> rawCommands;
	private ArrayList<Files> projectFiles;
	// private ArrayList<ServerThread> connectedServers;

	public HubServer() {
		connectedClients = new ArrayList<ClientThread>();
		comClients = new LinkedBlockingQueue<String>();
		rawCommands = new LinkedBlockingQueue<String>();
		ServerSocketFactory fact = ServerSocketFactory.getDefault();

		try {
			server = fact.createServerSocket(5000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new OutputProcessor(this).start();
		collector = new ClientCollector(this);
		collector.start();
	}

	public void run() {
		while (true) {
		     System.out.println("HubServer run method running...");
			String com;
			Command out;
			out = OutputProcessor.takeFromInputQueue();

			System.out.println(out.response());
			
			//processing the Command out
			
			OutputProcessor.addToOutputQueue(out.response());

			try {
				com = comClients.take();
				for (ClientThread client : connectedClients) {
					client.talkToClient(com);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<ClientThread> getConnectedClients() {
		return connectedClients;
	}

	public void addRawCommand(String command) {
		rawCommands.add(command);
	}

	private void addClient(ClientThread e) {
		connectedClients.add(e);
		connectedClients.get(connectedClients.size() - 1).start();
	}

	public synchronized void communicateWithClients(String com) {
		comClients.add(com);
	}

	private class ClientCollector extends Thread {
		private HubServer hub;

		public ClientCollector(HubServer hub) {
			this.hub = hub;
		}

		public void run() {
			try {
				System.out.println("Waiting for client on port " + hub.server.getLocalPort() + " at address "
						+ hub.server.getInetAddress().getLocalHost());
				while (true) {
					Socket client = null;
					client = hub.server.accept();
					
					System.out.println("Connected to " + client.getRemoteSocketAddress());
					ClientThread ct = new ClientThread(client);
					hub.addClient(ct);

					if (hub.getConnectedClients().size() == 1) {
						//Initiate starting the document
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
