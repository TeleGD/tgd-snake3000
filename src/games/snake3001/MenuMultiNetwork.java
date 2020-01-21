package games.snake3001;

import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import app.ui.Button;
import app.ui.ColorPicker;
import app.ui.TextField;
import app.ui.TGDComponent;
import app.ui.TGDComponent.OnClickListener;

import games.snake3001.network_tcp.Client;
import games.snake3001.network_tcp.DiscoverServerThread;
import games.snake3001.network_tcp.DiscoveryThread;
import games.snake3001.network_tcp.Serveur;

public class MenuMultiNetwork implements Client.SocketListener {

	private int longueurJeu=(int)(World.longueur*0.8);

	private int hauteurMenu=(int)(World.hauteur/1.5);
	private int longueurMenu= World.longueur/2;
	private int debutx=(longueurJeu-longueurMenu)/2+longueurMenu/15;
	private int debuty=(World.hauteur-hauteurMenu)/2+hauteurMenu/10;
	private int nJoueur=0;
	private int pas = World.hauteur/20;
	private int yn=debuty+pas;
	private int debutNom = longueurJeu/2 - longueurMenu/10;
	private Button boutonStart;
	public boolean enleve=false;
	private ColorPicker picker;
	private boolean affPicker=false;

	private DiscoverServerThread discoverServerThread;private TextField nomJoueursField;
	private Button choixCouleur;

	private ArrayList<Snake> snakes =new ArrayList<>();

	public void init(final GameContainer container, StateBasedGame game) {

		final Snake snake = findSnakeByIpAdress(World.ipAdress);

		nomJoueursField= new TextField(container , debutNom , yn , longueurMenu/3 , hauteurMenu/15 );
		nomJoueursField.setBackgroundColor(Color.black);
		nomJoueursField.setTextColor(snake.couleur);
		nomJoueursField.setPadding(5, 5, 0, 15);
		nomJoueursField.setOnlyFigures(false);
		nomJoueursField.setMaxNumberOfLetter(20);

		boutonStart = new Button("START",container,longueurJeu/2-longueurMenu/6,(World.hauteur+hauteurMenu)/2-8*hauteurMenu/75,longueurMenu/3,hauteurMenu/15);
		boutonStart.setBackgroundColor(Color.green);
		boutonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				enleve=true;
				World.serveur.sendStringToAllClients("demarrer");
				World.setSnakes(snakes.toArray(new Snake[snakes.size()]));
			}});

		picker = new ColorPicker(container,0,0, World.longueur/5, World.hauteur/4);

		choixCouleur = new Button(container,longueurJeu/2+longueurMenu/4,yn,hauteurMenu/15,hauteurMenu/15);
		choixCouleur.setBackgroundColor(snake.couleur);
		choixCouleur.setBackgroundColorEntered(new Color(255,255,255,100));
		choixCouleur.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				// TODO Auto-generated method stub
				picker.setY(choixCouleur.getY());
				picker.setColorSelected(snake.couleur);
				picker.setX(longueurJeu/2+longueurMenu/3);
				picker.setVisible(true);
				picker.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(TGDComponent componenent) {
						// TODO Auto-generated method stub
						snake.couleur=picker.getColorSelected();

						choixCouleur.setBackgroundColor(picker.getColorSelected());
						nomJoueursField.setTextColor(picker.getColorSelected());
						picker.setVisible(false);
					}});
			}});

	}

	private Snake findSnakeByIpAdress(String ipAdress){

		for(int i=0;i<snakes.size();i++){
			if(snakes.get(i).ipAdress.equals(ipAdress)){
				return snakes.get(i);
			}
		}
		return new Snake(Color.white,0,Input.KEY_RIGHT,Input.KEY_RIGHT,10,"default",2);
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		if (!enleve) {

			g.setColor(new Color(0,0,255));
			g.fillRect((longueurJeu-longueurMenu)/2, (World.hauteur-hauteurMenu)/2, longueurMenu, hauteurMenu);
			g.setColor(new Color(0,0,0));
			g.drawString("Nom : "+nJoueur, debutx, debuty);
			g.drawString("nombre de joueurs : "+nJoueur, debutx, debuty+40);

			for (int i = 0;i<snakes.size();i+=1) {
				yn = debuty+40 + i*pas;
				g.setColor(snakes.get(i).couleur);
				g.drawString("Nom Joueur n "+i+" : "+snakes.get(i).nom,debutx,yn+35);

			}

			if (affPicker) {
				picker.render(container, game, g);
			}

			nomJoueursField.render(container, game, g);
			choixCouleur.render(container, game, g);

			if(World.isServer){
				boutonStart.render(container, game, g);
			}
		}
	}

	public void update(GameContainer container, StateBasedGame game, int delta) {
		if(World.isServer){
			boutonStart.update(container, game, delta);
		}
		nomJoueursField.update(container, game, delta);
		choixCouleur.update(container, game, delta);

	}

	public void keyPressed(int i, char c) {
		if(i == Input.KEY_S){

			if(World.isServer ==false){
				World.isServer = true;

				discoverServerThread = new DiscoverServerThread(5000,15);
				discoverServerThread.start();

				snakes.add(new Snake(choixCouleur.getBackgroundColor(),0,Input.KEY_RIGHT,Input.KEY_LEFT,10,nomJoueursField.getText(),2));
				snakes.get(snakes.size()-1).ipAdress =World.ipAdress;

				World.serveur =new Serveur(8887);
				World.serveur.addOnClientConnectedListener(new Serveur.OnClientConnectedListener() {
					@Override
					public void onConnected(Socket socket) {

					}

					@Override
					public void onDisconnected(Socket socket) {

					}
				});
				World.serveur.addSocketListener(MenuMultiNetwork.this);
				World.serveur.start();
			}

		}else if(i == Input.KEY_C){

			World.isServer = false;

			DiscoveryThread thread = new DiscoveryThread();
			thread.addOnServerDetectedListener(new DiscoveryThread.OnServerDetectedListener() {
				@Override
				public void onServerDetected(String ipAdress) {
					addClient(ipAdress);
				}
			});
			thread.start();

			//addClient("localhost");

		}
	}

	private void addClient(String ipAdress) {
		World.client= new Client(ipAdress,8887);

		String message = "add_joueur;";
		message += ipAdress+";";

		Color c = choixCouleur.getBackgroundColor();
		message += nomJoueursField.getText()+";"+c.getRed()+";"+c.getGreen()+";"+c.getBlue()+";"+c.getAlpha();

		World.client.sendString(message);
		World.client.sendString("get_connected_players");
		World.client.addSocketListener(MenuMultiNetwork.this);
	}

	@Override
	public void onMessageSend(Socket socket, String message) {
		System.out.println("message send= "+message);
	}

	@Override
	public void onMessageReceived(Socket socket, String message) {
		System.out.println("message received= "+message);
		if(message.equals("get_connected_players")){

			message = "received_connected_players;"+snakes.size()+";";
			for(int i=0;i<snakes.size();i++){
				message += snakes.get(i).nom+";"+snakes.get(i).ipAdress+";"+snakes.get(i).couleur.getRed()+";"+snakes.get(i).couleur.getGreen()+";"+snakes.get(i).couleur.getBlue()+";"+snakes.get(i).couleur.getAlpha()+";";
			}

			World.serveur.sendStringToAllClients(message);

		}else if(message.startsWith("received_connected_players")){

			//client

			String split[] = message.split(Pattern.quote(";"));

			nJoueur = Integer.parseInt(split[1]);
			snakes.removeAll(snakes);

			for(int i=0;i<nJoueur;i++){
				String nom =split[2+6*i];
				String adresse =split[2+6*i+1];
				int r = Integer.parseInt(split[2+6*i+2]);
				int g = Integer.parseInt(split[2+6*i+3]);
				int b = Integer.parseInt(split[2+6*i+4]);
				int a = Integer.parseInt(split[2+6*i+5]);
				Color c = new Color(r,g,b,a);
				Snake s = new Snake(c,12+12*i,Input.KEY_RIGHT,Input.KEY_LEFT,10,nom,10);
				s.ipAdress = adresse;
				snakes.add(s);
			}
		}else if(message.startsWith("add_joueur")){

			String[] split = message.split(Pattern.quote(";"));
			String adresse =split[1];
			String nom =split[2];
			int r = Integer.parseInt(split[3]);
			int g = Integer.parseInt(split[4]);
			int b = Integer.parseInt(split[5]);
			int a = Integer.parseInt(split[6]);
			Color c = new Color(r,g,b,a);

			//int xinit =(100-nJoueur)/(nJoueur+1) + *((100-nJoueur)/(nJoueur+1)+1);

			snakes.add(new Snake(c,snakes.size()*12+12,Input.KEY_RIGHT,Input.KEY_LEFT,10,nom,10));
			snakes.get(snakes.size()-1).ipAdress = adresse;

		}else if(message.startsWith("demarrer")){
			enleve=true;
			World.setSnakes(snakes.toArray(new Snake[snakes.size()]));
		}
	}
}
