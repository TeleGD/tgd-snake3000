package games.snake3000.network;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import games.snake3000.MenuMultiNetwork;
import games.snake3000.World;

public class Peer implements Closeable {

	private MenuMultiNetwork menu;
	private World world;
	private Client client;
	private Server server;
	private BlockingQueue<Packet> queue;
	private Thread thread;

	public Peer(MenuMultiNetwork menu, World world, String game, int port) throws IOException {
		this.menu = menu;
		this.world = world;
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
		this.client = new Client(this, game, port);
		this.server = null;
	}

	protected void update() {}

	public void close() {
		this.thread.interrupt();
		if (this.client != null) {
			this.client.close();
		}
		if (this.server != null) {
			this.server.close();
		}
	}

	private void pushToMenu(Packet packet) {
		if (this.menu == null) {
			return;
		}
		this.menu.pullFromPeer(packet);
	}

	private void pushToWorld(Packet packet) {
		if (this.world == null) {
			return;
		}
		this.world.pullFromPeer(packet);
	}

	private void pushToLocalClient(String message) {
		if (this.client == null) {
			return;
		}
		this.client.pullFromPeer(message);
	}

	private void pushToServer(String message) {
		if (this.server == null) {
			return;
		}
		this.server.pullFromPeer(message);
	}

	public void pullFromMenu(String message) {
		// TODO: transmettre le bon type de message
		Packet packet = new Packet("1\n" + message, null, 0);
		this.queue.offer(packet);
		packet = new Packet("3\n" + message, null, 0);
		this.queue.offer(packet);
	}

	public void pullFromWorld(String message) {
		Packet packet = new Packet("5\n" + message, null, 0);
		this.queue.offer(packet);
	}

	public void pullFromLocalClient(Packet packet) {
		String serverIp = packet.getAddress().getHostAddress();
		int serverPort = packet.getPort();
		if (packet.getMessage() != null) {
			System.out.println("Le serveur " + serverIp + " se fait entendre depuis son port " + serverPort);
		} else {
			System.out.println("Le serveur " + serverIp + " ne se fait plus entendre depuis son port " + serverPort);
		}
		this.queue.offer(packet);
	}

	public void pullFromLocalServer(Packet packet) {
		String clientIp = packet.getAddress().getHostAddress();
		int clientPort = packet.getPort();
		if (packet.getMessage() != null) {
			System.out.println("Le client " + clientIp + " se fait entendre depuis son port " + clientPort);
		} else {
			System.out.println("Le client " + clientIp + " ne se fait plus entendre depuis son port " + clientPort);
		}
		this.queue.offer(packet);
	}

}
