package games.snake3000;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppFont;
import app.AppLoader;
import app.ui.Button;
import app.ui.TGDComponent;

import games.snake3000.network.Packet;
import games.snake3000.network.Peer;

public class World extends BasicGameState {

	private static int caseSize = 10;
	private static int columns = 100;
	private static int rows = 72;

	public static int getCaseSize() {
		return World.caseSize;
	}

	public static int getColumns() {
		return World.columns;
	}

	public static int getRows() {
		return World.rows;
	}

	private float widthBandeau = 280;

	private List<Bonus> bonuses;
	private List<Snake> snakes;
	private Random random;
	private Font font;
	private Audio music;
	private float musicPos;

	// private Button replay;
	private Button config; // version locale seulement
	private Button backMenu;
	private boolean jeuTermine;

	private Peer peer; // version réseau seulement
	private BlockingQueue<String> queue; // version réseau seulement

	private int ID;
	private int state;

	public World(int ID) {
		this.ID = ID;
		this.state = 0;
	}

	@Override
	public int getID() {
		return this.ID;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au chargement du programme */
		this.random = new Random();
		this.snakes = new ArrayList<Snake>();
		this.bonuses = new ArrayList<Bonus>();
		this.font = AppLoader.loadFont("/fonts/vt323.ttf", AppFont.BOLD, 20);
		this.music = AppLoader.loadAudio("/musics/snake3000/hymne_russe.ogg");
		this.musicPos = 0f;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée à l'apparition de la page */
		if (this.state == 0) {
			this.play(container, game);
		} else if (this.state == 2) {
			this.resume(container, game);
		}
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée à la disparition de la page */
		if (this.state == 1) {
			this.pause(container, game);
		} else if (this.state == 3) {
			this.stop(container, game);
			this.state = 0; // TODO: remove
		}
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		/* Méthode exécutée environ 60 fois par seconde */
		for (Bonus bonus: this.bonuses) {
			bonus.render(container, game, context);
		}
		for (Snake snake: this.snakes) {
			snake.render(container, game, context);
		}

		int width = container.getWidth();
		int height = container.getHeight();

		context.setColor(new Color(150,150,150));
		context.fillRect(width-widthBandeau+2,0,widthBandeau,height);
		context.setColor(new Color(170,170,170));
		context.fillRect(width-widthBandeau+4,0,widthBandeau,height);
		context.setColor(new Color(200,200,200));
		context.fillRect(width-widthBandeau+6,0,widthBandeau,height);

		context.setFont(font);
		context.setColor(Color.black);
		context.drawString("SNAKE 3000 ! ",width-widthBandeau+20,20);

		context.setColor(new Color(150,150,150));
		context.fillRect(width-widthBandeau+6,60,widthBandeau,5);
		context.resetFont();

		if(jeuTermine){
			context.setColor(Color.black);
			context.fillRoundRect(width/2-75,height/2-50,150,100,20);
			context.setColor(Color.white);
			context.fillRoundRect(width/2-75+4,height/2-50+4,150-8,92,20);
			context.setColor(Color.black);
			context.setFont(font);
			context.drawString("Perdu !", width/2-30,height/2-30);
		}

		List<Snake> snakes = this.snakes;
		for (int i = 0, li = snakes.size(); i < li; ++i) {
			Snake snake = snakes.get(i);
			context.setColor(snake.getColor());
			context.drawString(snake.getName() + " : " + snake.getScore(), width - widthBandeau + 20, 100 + 50 * i + 20);
		}
		// replay.render(container, game, context);
		if (this.peer == null) { // version locale seulement
			config.render(container, game, context);
		}
		backMenu.render(container, game, context);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		/* Méthode exécutée environ 60 fois par seconde */
		Input input = container.getInput();
		if (input.isKeyDown(Input.KEY_ESCAPE)) {
			this.setState(1);
			game.enterState(2, new FadeOutTransition(), new FadeInTransition());
		}
		// replay.update(container, game,delta);
		if (this.peer == null) { // version locale seulement
			config.update(container,game,delta);
		}
		backMenu.update(container, game,delta);
		if (!jeuTermine) {
			Collections.sort(snakes, new Comparator<Snake>() {

				@Override
				public int compare(Snake snakeA, Snake snakeB) {
					int scoreA = snakeA.getScore();
					int scoreB = snakeB.getScore();
					if (scoreA < scoreB) {
						return 1;
					}
					if (scoreA > scoreB) {
						return -1;
					}
					return 0;
				}

			});
			jeuTermine = isFini();
		}
		if (!jeuTermine) {
			if (this.peer != null) { // version réseau seulement
				this.pushToPeer("");
			}
			Random random = this.random;
			if (random.nextFloat() >= .99f) {
				bonuses.add(Bonus.createRandomBonus(random));
			}
			for (Bonus bonus: bonuses) {
				bonus.update(container, game, delta);
			}
			for (Snake snake: snakes) {
				snake.update(container, game, delta);
			}
			List<Bonus> bonuses = this.bonuses;
			Map<Bonus, Snake> bonusesToApply = new HashMap<Bonus, Snake>();
			List<Snake> snakes = this.snakes;
			Set<Snake> snakesToKill = new HashSet<Snake>();
			for (Bonus bonus: bonuses) {
				for (Snake snake: snakes) {
					if (bonus.contains(snake.getHead())) {
						bonusesToApply.put(bonus, snake);
						break;
					}
				}
			}
			for (Snake snake: snakes) {
				for (Snake otherSnake: snakes) {
					if (otherSnake != snake && otherSnake.contains(snake.getHead())) {
						snakesToKill.add(snake);
						break;
					}
				}
			}
			for (Map.Entry<Bonus, Snake> entry: bonusesToApply.entrySet()) {
				entry.getKey().apply(this, entry.getValue());
			}
			for (Snake snake: snakesToKill) {
				snake.kill();
			}
		}
	}

	public void play(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au début du jeu */
		this.music.playAsMusic(1, .3f, true);
		container.getInput().clearKeyPressedRecord();
		this.bonuses.clear();
		this.jeuTermine = false;
		// TODO: initialiser peer et queue
		this.peer = null;
		this.queue = new LinkedBlockingQueue<String>();

		int width = container.getWidth();
		int height = container.getHeight();

		// replay = new Button(container,width - widthBandeau+20, height-150,widthBandeau-40,40);
		// replay.setText("REJOUER");
		// replay.setBackgroundColor(Color.black);
		// replay.setBackgroundColorEntered(Color.white);
		// replay.setTextColor(Color.white);
		// replay.setTextColorEntered(Color.black);
		// replay.setCornerRadius(25);
		// replay.setOnClickListener(new TGDComponent.OnClickListener() {
		//
		// 	@Override
		// 	public void onClick(TGDComponent componenent) {
		// 		World.this.setState(3);
		// 		game.enterState(3);
		// 		System.out.println(World.this.snakes.size());
		// 		((MenuMulti) game.getState(3)).startGame(World.this);
		// 		game.enterState(World.this.getID());
		// 		System.out.println(World.this.snakes.size());
		// 	}
		//
		// });

		if (this.peer == null) { // version locale seulement
			config = new Button(container,width - widthBandeau+20, height-100,widthBandeau-40,40);
			config.setText("CONFIGURATION");
			config.setBackgroundColor(Color.black);
			config.setBackgroundColorEntered(Color.white);
			config.setTextColor(Color.white);
			config.setTextColorEntered(Color.black);
			config.setCornerRadius(25);
			config.setOnClickListener(new TGDComponent.OnClickListener() {

				@Override
				public void onClick(TGDComponent componenent) {
					World.this.setState(3);
					game.enterState(3, new FadeOutTransition(), new FadeInTransition());
				}

			});
		}

		backMenu = new Button(container,width - widthBandeau+20, height-50,widthBandeau-40,40);
		backMenu.setText("RETOUR AU MENU");
		backMenu.setBackgroundColor(Color.black);
		backMenu.setBackgroundColorEntered(Color.white);
		backMenu.setTextColor(Color.white);
		backMenu.setTextColorEntered(Color.black);
		backMenu.setCornerRadius(25);
		backMenu.setOnClickListener(new TGDComponent.OnClickListener() {
			@Override
			public void onClick(TGDComponent componenent) {
				World.this.setState(3);
				game.enterState(1, new FadeOutTransition(), new FadeInTransition());
			}
		});
	}

	public void pause(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la mise en pause du jeu */
		Audio music = this.music;
		this.musicPos = music.getPosition();
		music.stop();
	}

	public void resume(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la reprise du jeu */
		Audio music = this.music;
		music.playAsMusic(1, .3f, true);
		music.setPosition(this.musicPos);
		container.getInput().clearKeyPressedRecord();
	}

	public void stop(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois à la fin du jeu */
		this.music.stop();
		this.snakes.clear();
		this.bonuses.clear();
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return this.state;
	}

	public void addBonus(Bonus bonus) {
		this.bonuses.add(bonus);
	}

	public void removeBonus(Bonus bonus) {
		this.bonuses.remove(bonus);
	}

	public void setSnakes(Snake[] snakes) {
		this.snakes = new ArrayList<Snake>(Arrays.asList(snakes));
	}

	public Snake[] getSnakes() {
		List<Snake> snakes = this.snakes;
		return snakes.toArray(new Snake[snakes.size()]);
	}

	private boolean isFini() {
		List<Snake> snakes = this.snakes;
		int size = snakes.size();
		if (size <= 1) {
			return size == 0 || snakes.get(0).isDead();
		}
		boolean theShowMustGoOn = false;
		for (Snake snake: snakes) {
			if (snake.isDead()) {
				continue;
			}
			if (theShowMustGoOn) {
				return false;
			}
			theShowMustGoOn = true;
		}
		return true;
	}

	public void fromString(String string) { // version réseau seulement
		if (string.length() != 0 && !string.endsWith("\n")) {
			string += "\n";
		}
		try {
			BufferedReader reader = new BufferedReader(new StringReader(string));
			String line1;
			List<Bonus> bonuses = new ArrayList<Bonus>();
			while ((line1 = reader.readLine()) != null && !line1.equals("")) {
				((Bonus) null).fromString(line1.substring(1).replaceAll(";", "\n"));
				bonuses.add(null);
			}
			// this.bonuses.clear();
			// this.bonuses.addAll(bonuses);
			List<Snake> snakes = new ArrayList<Snake>();
			while ((line1 = reader.readLine()) != null) {
				((Snake) null).fromString(line1.substring(1).replaceAll(";", "\n"));
				snakes.add(null);
			}
			// this.snakes.clear();
			// this.snakes.addAll(snakes);
			reader.close();
		} catch (Exception error) {}
	}

	public String toString() { // version réseau seulement
		String string = "";
		try {
			BufferedWriter writer = new BufferedWriter(new StringWriter());
			for (Bonus bonus: this.bonuses) {
				writer.write("\t" + bonus.toString().replaceAll("\n", ";") + "\n");
			}
			writer.write("\n");
			for (Snake snake: this.snakes) {
				writer.write("\t" + snake.toString().replaceAll("\n", ";") + "\n");
			}
			string = writer.toString();
			writer.close();
		} catch (Exception error) {}
		return string;
	}

	private void pushToPeer(String message) { // version réseau seulement
		if (this.peer == null) {
			return;
		}
		this.peer.pullFromWorld(message);
	}

	public void pullFromPeer(Packet packet) { // version réseau seulement
		String message = packet.getMessage();
		this.queue.offer(message);
	}

}
