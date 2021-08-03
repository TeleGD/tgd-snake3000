package games.snake3000.network;

import java.net.InetAddress;

public class Packet {

	private String message;
	private InetAddress address;
	private int port;

	public Packet(String message, InetAddress address, int port) {
		this.message = message;
		this.address = address;
		this.port = port;
	}

	public String getMessage() {
		return this.message;
	}

	public InetAddress getAddress() {
		return this.address;
	}

	public int getPort() {
		return this.port;
	}

}
