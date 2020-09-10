package games.snake3000.network_tcp;

import java.net.Socket;

public class MainTest implements Client.SocketListener {
	private Server server;
	private Client client;

	private MainTest(){

		//A LANCER SUR L'ORDI SERVEUR
		//new DiscoverServerThread(2000,20).start(); //Lance le thread pour que les clients auto-détectent ce serveur
		//on lance le serveur TCP sur le port 8887
		/*
		server = new Server(8887);
		server.addSocketListener(this); //informe qu'on va recevoir les messages dans les methodes plus bas
		server.start();
		server.addSocketListener(new Client.SocketListener(){

			@Override
			public void onMessageSend(Socket socket, String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMessageReceived(Socket socket, String message) {
				// TODO Auto-generated method stub

			}} );
		*/
		/*
		 * On peut faire coté serveur:
		 *
		 * => Une fois la connexion etablit avec un client ou plusieurs:
		 *
		 * -  server.sendStringToAllClients(messageToSend); //pour envoyer un message a tout le monde
		 * -  server.sendStringToClient(socket, messageToSend) // pour envoyer un message a un client en particulier
		 * -  server.getSockets() //Retourne les sockets des clients actuellement connectés
		 * -  server.getNbClientsConnected() //Retourne le nombre de clients connecte
		 *
		 * => Les listeners, pour etre notifier:
		 *
		 * -  server.addOnClientConnectedListener(OnClientConnectedListener onClientConnectedListener);  // quand un client se connecte ou se deconnecte
		 * -  server.addSocketListener(SocketListener socketListener); // quand on recoit ou on emet un message
		 */

		//A LANCER SUR UN ORDI CLIENT
		DiscoveryThread discoveryThread = new DiscoveryThread();
		discoveryThread.addOnServerDetectedListener(new DiscoveryThread.OnServerDetectedListener(){

			@Override
			public void onServerDetected(String ipAdress) {
				//L'adresse du serveur a été trouvé
				client = new Client(ipAdress,8888);
				client.addSocketListener(MainTest.this);
				client.sendString("salut");

			}});

		discoveryThread.start(); // Lance la recherche du serveur

		/*
		 * On peut faire coté client:
		 *
		 * => Une fois la connexion etablit avec un client ou plusieurs:
		 *
		 * -  server.sendString(messageToSend); //pour envoyer un message au serveur
		 *
		 * => Les listeners, pour etre notifier:
		 *
		 * -  server.addSocketListener(SocketListener socketListener); // quand on recoit ou on emet un message
		 */

	}

	public static void main(String[] args) {
		new MainTest();
	}

	@Override
	public void onMessageSend(Socket socket, String message) {
		System.out.println("Message send : "+message);

	}

	@Override
	public void onMessageReceived(Socket socket, String message) {
		System.out.println("Message received from "+socket.getInetAddress().getHostAddress()+": "+message);
	}

}
