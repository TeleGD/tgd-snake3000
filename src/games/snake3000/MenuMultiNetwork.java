package games.snake3000;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

import games.snake3000.network.Packet;
import games.snake3000.network.Peer;

public class MenuMultiNetwork extends BasicGameState {

	private int longueurJeu;
	private int hauteurMenu;
	private int longueurMenu;
	private int debutx;
	private int debuty;
	private int nJoueur;
	private int pas;
	private Button boutonStart;
	private ColorPicker picker;

	private TextField nomJoueursField;
	private Button choixCouleur;

	private List<Snake> snakes;

	private Peer peer;
	private BlockingQueue<String> queue;

	private int ID;

	public MenuMultiNetwork(int ID) {
		this.ID = ID;
	}

	@Override
	public int getID() {
		return this.ID;
	}

	public void init(GameContainer container, StateBasedGame game) {
		Snake snake = new Snake(Color.white,"",Input.KEY_LEFT,Input.KEY_RIGHT,0);
		snakes = new ArrayList<Snake>();
		snakes.add(snake);
		this.peer = null;
		this.queue = new LinkedBlockingQueue<String>();

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
				MenuMultiNetwork.this.pushToPeer("");
				game.enterState(5, new FadeOutTransition(), new FadeInTransition());
			}});

		picker = new ColorPicker(container,debutx,0,width/5,height/4);
		picker.setVisible(false);

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
		Input input = container.getInput();
		if (input.isKeyDown(Input.KEY_ESCAPE)) {
			game.enterState(1, new FadeOutTransition(), new FadeInTransition());
		}
		if (this.peer != null) { // TODO: vérifier que l'on est en mode serveur et qu'on a bien au moins un joueur
			boutonStart.update(container, game, delta);
		}
		picker.update(container, game, delta);
		nomJoueursField.update(container, game, delta);
		choixCouleur.update(container, game, delta);
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) {
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
			if (this.peer != null) { // TODO: vérifier que l'on est en mode serveur et qu'on a bien au moins un joueur
				boutonStart.render(container, game, g);
			}
			picker.render(container, game, g);
			nomJoueursField.render(container, game, g);
			choixCouleur.render(container, game, g);
	}

	private void pushToPeer(String message) { // version réseau seulement
		if (this.peer == null) {
			return;
		}
		this.peer.pullFromMenu(message);
	}

	public void pullFromPeer(Packet packet) { // version réseau seulement
		String message = packet.getMessage();
		this.queue.offer(message);
	}

}
