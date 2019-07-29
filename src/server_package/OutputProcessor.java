package server_package;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputProcessor extends Thread {

	private HubServer hub;
	private static BlockingQueue<String> clientOutputQueue;
	private static BlockingQueue<Command> clientInputQueue;
	
	public OutputProcessor(HubServer hub) {
		this.hub = hub;
		clientOutputQueue = new LinkedBlockingQueue<String>();
		clientInputQueue = new LinkedBlockingQueue<Command>();
		System.out.println("Queues Active");
	}

	public void run() {
		while (true) {
			String com;
			String input;
			
			try {
				com = clientOutputQueue.take();
				hub.communicateWithClients(com);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static synchronized void addToOutputQueue(String com) {
		clientOutputQueue.add(com);
	}
	
	public static void addToInputQueue(Command input) {
	     System.out.println("adding input");
		clientInputQueue.add(input);
		System.out.println("input added");
	}
	
	public synchronized static BlockingQueue<Command> getInputQueue() {
	    return clientInputQueue;
	}
	
	public synchronized static Command takeFromInputQueue() {
		try {
			return clientInputQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
