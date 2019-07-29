package server_package;

public class Main {

	public static void main(String[] args) {
		Thread t = new HubServer();
		t.start();
	}
}
