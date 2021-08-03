package games.snake3000.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Closeable {

	private Peer peer;
	private String header;
	private int headerLength;
	private InetAddress serverAddress;
	private int serverPort;
	private InetAddress clientAddress;
	private int clientPort;
	private DatagramSocket socket;
	private BlockingQueue<Packet> queue;
	private Thread thread;

	public Client(Peer peer, String game, int port) throws IOException {
		InetAddress address = InetAddress.getByName("255.255.255.255");
		this.peer = peer;
		this.header = game.replaceAll("\n", "\t") + "\n";
		this.headerLength = header.length();
		this.serverAddress = address;
		this.serverPort = port;
		this.socket = new DatagramSocket();
		this.clientAddress = this.socket.getLocalAddress();
		this.clientPort = this.socket.getLocalPort();
		String clientIp = this.clientAddress.getHostAddress();
		System.out.println("Le client " + clientIp + " parle désormais sur le port " + this.clientPort);
		this.queue = new LinkedBlockingQueue<Packet>();
		this.thread = new Thread() {

			public void run() {
				int state = 0;
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
		String clientIp = this.clientAddress.getHostAddress();
		System.out.println("Le client " + clientIp + " ne parle plus sur le port " + this.clientPort);
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
		this.peer.pullFromLocalClient(packet);
	}

	public void pullFromPeer(String message) {
		Packet packet = new Packet("6\n" + message, this.clientAddress, this.clientPort);
		this.queue.offer(packet);
		// TODO: remplacer l'adresse du client par l'adresse du serveur à l'envoi
	}

	public InetAddress getAddress() {
		return this.clientAddress;
	}

	public int getPort() {
		return this.clientPort;
	}

}
