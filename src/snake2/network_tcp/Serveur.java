package snake2.network_tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Serveur extends Thread implements Runnable{

	private volatile boolean isActive;
	private int port;
	private ArrayList<Socket> sockets =new ArrayList<Socket>();

	private Client.SocketListener socketListener;
	private OnClientConnectedListener onClientConnectedListener;

	public Serveur(int port){
		this.port = port;
	}

	@Override
	public void run(){
		isActive=true;

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(0); // On veut un timeout infini du serveur donc 0

			System.out.println("Serveur TCP démarré sur le port "+port);

			while(isActive){

				//On attend qu'un client se connecte
				Socket socket = serverSocket.accept();


				//On traite les connexions avec ce client dans un thread a part pour pas monopoliser le serveur
				ThreadClient threadClient = new ThreadClient(socket);
				threadClient.start();

				//on tient a jour la liste des sockets qui nous donne directement le nombre de clients connectés a ce serveur
				sockets.add(socket);

				//On notifie l'ecouteur si il existe
				if(onClientConnectedListener != null) onClientConnectedListener.onConnected(socket);


			}
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean sendStringToClient(Socket socket,String messageToSend){
		if(socket==null || socket.isClosed() || !socket.isConnected()){
			System.out.println("Socket Invalide"); // Le socket est mort, on ne peut rien envoye
			return false;
		}

		try {
			StreamUtils.writeToOutputStream(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), messageToSend);
			if(socketListener != null)socketListener.onMessageSend(socket, messageToSend);
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public void sendStringToAllClients(String messageToSend) {
		for(int i=0;i<sockets.size();i++){
			sendStringToClient(sockets.get(i),messageToSend);
		}
	}

	public class ThreadClient extends Thread implements Runnable{

		private Socket socket;

		public ThreadClient(Socket socket) {
			this.socket = socket;

		}

		@Override
		public void run(){
			try {
				readAnswer(socket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sockets.remove(socket);

			if(onClientConnectedListener != null) onClientConnectedListener.onDisconnected(socket);

		}


	}

	public void readAnswer(Socket socket) throws IOException {
		BufferedReader dis =  new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String messageReceived;

		while(!socket.isClosed() && socket.isConnected()){
			messageReceived = StreamUtils.readFromInputStream(dis);
			if(socketListener != null && messageReceived.length()>0) socketListener.onMessageReceived(socket, messageReceived);
		}
	}


	public void addSocketListener(Client.SocketListener socketListener) {
		this.socketListener=socketListener;
	}

	public void arreter() {
		isActive=false;
	}

	public int getNbClientsConnected() {
		return sockets.size();
	}

	public ArrayList<Socket> getSockets() {
		return sockets;
	}

	public void addOnClientConnectedListener(OnClientConnectedListener onClientConnectedListener) {
		this.onClientConnectedListener = onClientConnectedListener;
	}

	public interface OnClientConnectedListener {
		void onConnected(Socket socket);
		void onDisconnected(Socket socket);
	}



}
