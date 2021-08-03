package games.snake3000.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Closeable {

	private Peer peer;
	private String header;
	private int headerLength;
	private InetAddress[] clientAddresses;
	private int[] clientPorts;
	private InetAddress serverAddress;
	private int serverPort;
	private DatagramSocket socket;
	private BlockingQueue<Packet> queue;
	private Thread thread;

	public Server(Peer peer, String game, int port) throws IOException {
		this.peer = peer;
		this.header = game.replaceAll("\n", "\t") + "\n";
		this.headerLength = header.length();
		this.clientAddresses = new InetAddress[7];
		this.clientPorts = new int[7];
		this.socket = new DatagramSocket(port);
		this.serverAddress = this.socket.getLocalAddress();
		this.serverPort = this.socket.getLocalPort();
		String serverIp = serverAddress.getHostAddress();
		System.out.println("Le serveur " + serverIp + " écoute désormais sur le port " + serverPort);
		this.queue = new LinkedBlockingQueue<Packet>();
		this.thread = new Thread() {

			public void run() {
				int state = 0;
				int[] states = new int[7];
				int[] timeouts = new int[7]; // TODO: adapter la durée en fonction de l'état
				for (;;) {
					switch (state) {
					}
				}
			}

		};
	}

	public void close() {
		if (this.socket == null) {
			return;
		}
		InetAddress serverAddress = this.socket.getLocalAddress();
		int serverPort = this.socket.getLocalPort();
		String serverIp = serverAddress.getHostAddress();
		System.out.println("Le serveur " + serverIp + " n'écoute plus sur le port " + serverPort);
		this.thread.interrupt();
		this.socket.close();
	}

	private void send(Packet packet, boolean broadcast) {
		try {
			this.socket.setBroadcast(broadcast);
			String message = this.header + packet.getMessage();
			byte[] buffer = message.getBytes();
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(datagramPacket);
		} catch (IOException exception) {
			exception.printStackTrace(); // TODO: enlever
		}
	}

	private Packet receive(int timeout) {
		Packet packet = null;
		try {
			this.socket.setSoTimeout(timeout);
			byte[] buffer = new byte[8192];
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			socket.receive(datagramPacket);
			String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
			if (message.startsWith(this.header)) {
				message = message.substring(this.headerLength);
				InetAddress address = datagramPacket.getAddress();
				int port = datagramPacket.getPort();
				packet = new Packet(message, address, port);
			}
		} catch (IOException exception) {
			exception.printStackTrace(); // TODO: enlever
		}
		return packet;
	}

	private void pushToPeer(Packet packet) {
		if (this.peer == null) {
			return;
		}
		this.peer.pullFromLocalServer(packet);
	}

	public void pullFromPeer(String message) {
		Packet packet = new Packet("5\n" + message, this.serverAddress, this.serverPort);
		this.queue.offer(packet);
		// TODO: remplacer l'adresse du serveur par l'adresse du client à l'envoi
	}

	public InetAddress getAddress() {
		return this.serverAddress;
	}

	public int getPort() {
		return this.serverPort;
	}

}
