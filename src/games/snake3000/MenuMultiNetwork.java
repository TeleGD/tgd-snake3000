package games.snake3000;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.ui.Button;
import app.ui.ColorPicker;
import app.ui.TextField;
import app.ui.TGDComponent;
import app.ui.TGDComponent.OnClickListener;

import games.snake3000.network_tcp.Client;
import games.snake3000.network_tcp.DiscoverServerThread;
import games.snake3000.network_tcp.DiscoveryThread;
import games.snake3000.network_tcp.Server;

public class MenuMultiNetwork extends BasicGameState implements Client.SocketListener {

	private int longueurJeu;
	private int hauteurMenu;
	private int longueurMenu;
	private int debutx;
	private int debuty;
	private int nJoueur;
	private int pas;
	private Button boutonStart;
	private ColorPicker picker;
	private boolean affPicker=false;

	private DiscoverServerThread discoverServerThread;private TextField nomJoueursField;
	private Button choixCouleur;

	private ArrayList<Snake> snakes =new ArrayList<>();

	private List<String> messages = new ArrayList<String>();

	private int ID;

	public MenuMultiNetwork(int ID) {
		this.ID = ID;
	}

	@Override
	public int getID() {
		return this.ID;
	}

	public void init(GameContainer container, StateBasedGame game) {
		World world = (World) game.getState(5);
		final Snake snake = findSnakeByIpAddress(world.getIpAddress());

		int width = container.getWidth();
		int height = container.getHeight();
		longueurJeu=(int)(container.getWidth()*.8);

		hauteurMenu=(int)(container.getHeight()/1.45);
		longueurMenu=container.getWidth()/2;
		debutx=(longueurJeu-longueurMenu)/2+longueurMenu/15;
		debuty=(container.getHeight()-hauteurMenu)/2+hauteurMenu/15;
		nJoueur=0;
		pas = container.getHeight()/20;
		int debutNom = longueurJeu/2 - longueurMenu/10;

		int yn=debuty+pas;
		nomJoueursField= new TextField(container , debutNom , yn , longueurMenu/3 , hauteurMenu/15 );
		nomJoueursField.setBackgroundColor(Color.black);
		nomJoueursField.setTextColor(snake.getColor());
		nomJoueursField.setPadding(5, 5, 0, 15);
		nomJoueursField.setOnlyFigures(false);
		nomJoueursField.setMaxNumberOfLetter(20);

		boutonStart = new Button("START",container,longueurJeu/2-longueurMenu/6,(height+hauteurMenu)/2-8*hauteurMenu/75,longueurMenu/3,hauteurMenu/15);
		boutonStart.setBackgroundColor(Color.green);
		boutonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				world.getServer().sendStringToAllClients("demarrer");
				world.setSnakes(snakes.toArray(new Snake[snakes.size()]));
				game.enterState(5, new FadeOutTransition(), new FadeInTransition());
			}});

		picker = new ColorPicker(container,debutx,0,width/5,height/4);

		choixCouleur = new Button(container,longueurJeu/2+longueurMenu/4,yn,hauteurMenu/15,hauteurMenu/15);
		choixCouleur.setBackgroundColor(snake.getColor());
		choixCouleur.setBackgroundColorEntered(new Color(255,255,255,100));
		choixCouleur.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				// TODO Auto-generated method stub
				picker.setY(choixCouleur.getY());
				picker.setColorSelected(snake.getColor());
				picker.setX(longueurJeu/2+longueurMenu/3);
				picker.setVisible(true);
				picker.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(TGDComponent componenent) {
						// TODO Auto-generated method stub
						snake.setColor(picker.getColorSelected());

						choixCouleur.setBackgroundColor(picker.getColorSelected());
						nomJoueursField.setTextColor(picker.getColorSelected());
						picker.setVisible(false);
					}});
			}});

	}

	public void update(GameContainer container, StateBasedGame game, int delta) {
		World world = (World) game.getState(5);
		Input input = container.getInput();
		if(input.isKeyPressed(Input.KEY_S)){

			if(world.isServer() ==false){
				world.toggleServer();

				discoverServerThread = new DiscoverServerThread(5000,15);
				discoverServerThread.start();

				snakes.add(new Snake(choixCouleur.getBackgroundColor(),nomJoueursField.getText(),Input.KEY_LEFT,Input.KEY_RIGHT,0));
				snakes.get(snakes.size()-1).setIpAddress(world.getIpAddress());

				world.setServer(new Server(8887));
				world.getServer().addOnClientConnectedListener(new Server.OnClientConnectedListener() {
					@Override
					public void onConnected(Socket socket) {

					}

					@Override
					public void onDisconnected(Socket socket) {

					}
				});
				world.getServer().addSocketListener(this);
				world.getServer().start();
			}

		}else if(input.isKeyPressed(Input.KEY_C)){

			DiscoveryThread thread = new DiscoveryThread();
			thread.addOnServerDetectedListener(new DiscoveryThread.OnServerDetectedListener() {
				@Override
				public void onServerDetected(String ipAddress) {
					addClient(world, ipAddress);
				}
			});
			thread.start();

			//addClient("localhost");

		}
		if(world.isServer()){
			boutonStart.update(container, game, delta);
		}
		nomJoueursField.update(container, game, delta);
		choixCouleur.update(container, game, delta);
		for (String message: this.messages) { // IDEA: vérifier qu'on n'accède jamais de manière concurrente à cette liste pendant cette boucle
			if(message.equals("get_connected_players")){

				message = "received_connected_players;"+snakes.size()+";";
				for(int i=0;i<snakes.size();i++){
					message += snakes.get(i).getName()+";"+snakes.get(i).getIpAddress()+";"+snakes.get(i).getColor().getRed()+";"+snakes.get(i).getColor().getGreen()+";"+snakes.get(i).getColor().getBlue()+";"+snakes.get(i).getColor().getAlpha()+";";
				}

				world.getServer().sendStringToAllClients(message);

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
					Snake s = new Snake(c,nom,Input.KEY_LEFT,Input.KEY_RIGHT,12+12*i);
					s.setIpAddress(adresse);
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

				snakes.add(new Snake(c,nom,Input.KEY_RIGHT,Input.KEY_LEFT,snakes.size()*12+12));
				snakes.get(snakes.size()-1).setIpAddress(adresse);

			}else if(message.startsWith("demarrer")){
				world.setSnakes(snakes.toArray(new Snake[snakes.size()]));
				game.enterState(5, new FadeOutTransition(), new FadeInTransition());
			}
			this.messages.clear();
		}

	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		World world = (World) game.getState(5);
			int height = container.getHeight();
			g.setColor(new Color(0,0,255));
			g.fillRect((longueurJeu-longueurMenu)/2, (height-hauteurMenu)/2, longueurMenu, hauteurMenu);
			g.setColor(new Color(0,0,0));
			g.drawString("Nom : "+nJoueur, debutx, debuty);
			g.drawString("nombre de joueurs : "+nJoueur, debutx, debuty+40);

			for (int i = 0;i<snakes.size();i+=1) {
				int yn = debuty+40 + i*pas;
				g.setColor(snakes.get(i).getColor());
				g.drawString("Nom Joueur n "+i+" : "+snakes.get(i).getName(),debutx,yn+35);

			}

			if (affPicker) {
				picker.render(container, game, g);
			}

			nomJoueursField.render(container, game, g);
			choixCouleur.render(container, game, g);

			if(world.isServer()){
				boutonStart.render(container, game, g);
			}
	}

	private void addClient(World world, String ipAddress) {
		world.setClient(new Client(ipAddress,8887));

		String message = "add_joueur;";
		message += ipAddress+";";

		Color c = choixCouleur.getBackgroundColor();
		message += nomJoueursField.getText()+";"+c.getRed()+";"+c.getGreen()+";"+c.getBlue()+";"+c.getAlpha();

		world.getClient().sendString(message);
		world.getClient().sendString("get_connected_players");
		world.getClient().addSocketListener(this);
	}

	@Override
	public void onMessageSend(Socket socket, String message) {
		System.out.println("message send= "+message);
	}

	@Override
	public void onMessageReceived(Socket socket, String message) {
		System.out.println("message received= "+message);
		this.messages.add(message);
	}

	private Snake findSnakeByIpAddress(String ipAddress){

		for(int i=0;i<snakes.size();i++){
			if(snakes.get(i).getIpAddress().equals(ipAddress)){
				return snakes.get(i);
			}
		}
		return new Snake(Color.white,"default",Input.KEY_LEFT,Input.KEY_RIGHT,0);
	}

}
