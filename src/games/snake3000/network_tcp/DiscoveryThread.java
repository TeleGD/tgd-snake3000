package games.snake3000.network_tcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class DiscoveryThread extends Thread implements Runnable{

	private static final String MESSAGE_TO_DETECT = "SomeRequestData";
	private static final String MESSAGE_DETECTED = "oui c'est moi !";
	private static final int PORT = 8889;

	private OnServerDetectedListener listener;

	@Override
	public void run(){
		DatagramSocket socket;
		try {
			byte[] recvBuf;
			DatagramPacket receivePacket;
			String msgReceived;

			//On accepte les connexions de tous le monde

			do{
				recvBuf = new byte[15000];
				receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

				System.out.println("on ecoute sur le port : "+PORT);
				socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
				socket.setBroadcast(true);

				socket.receive(receivePacket);
				socket.close();

				msgReceived = new String(receivePacket.getData(), 0,receivePacket.getLength());
				System.out.println("MESSAGE RECU DE LA PART DU SERVEUR : "+msgReceived);

			}while(!msgReceived.equals(MESSAGE_TO_DETECT));

			System.out.println("MESSAGE RECU DE LA PART DU SERVEUR : "+msgReceived);

			byte[] sendData = MESSAGE_DETECTED.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), PORT);
			socket = new DatagramSocket();
			socket.send(sendPacket);
			socket.close();

			System.out.println("MESSAGE ENVOYE AU SERVEUR : "+MESSAGE_DETECTED);

			listener.onServerDetected(receivePacket.getAddress().getHostAddress());

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void addOnServerDetectedListener(OnServerDetectedListener listener){
		this.listener=listener;
	}
	public interface OnServerDetectedListener{
		void onServerDetected(String ipAdress);
	}
}
