package server_package;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ServerSocketFactory;

import client_package.ClientThread;

public class HubServer extends Thread {

	private ServerSocket server;
	//List of connected Clients
	private ArrayList<ClientThread> connectedClients;
	//Private class that waits for clients to connect
	private ClientCollector collector;
	private BlockingQueue<String> rawCommands;
	// private ArrayList<ServerThread> connectedServers;

	public HubServer() {
		connectedClients = new ArrayList<ClientThread>();
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
			Command com;
		     com = OutputProcessor.takeFromOutputQueue();
               for (ClientThread client : connectedClients) {
                    System.out.println(client.getClient().getInetAddress());
                    if(client.getClient().getInetAddress().equals(com.sentFrom()))
                         client.talkToClient(com.output());
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

	private class ClientCollector extends Thread {
		private HubServer hub;

		public ClientCollector(HubServer hub) {
			this.hub = hub;
		}

		public void run() {
			try {
				System.out.println("Waiting for client on port " + hub.server.getLocalPort() + " at address "
						+ hub.server.getInetAddress().getLocalHost().getHostAddress());
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
