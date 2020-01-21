package games.snake3000;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppFont;
import app.AppLoader;
import app.ui.Button;
import app.ui.TGDComponent;

public class World extends BasicGameState {

	private static int nbcasesh=72;
	private static int nbcasesl=128;
	private static MenuMulti menu;
	public static int longueur=1280;
	public static int hauteur=720;

	private float widthBandeau = 280;
	private static boolean jeuDemarre = false;
	private static ArrayList<Bonus> bonus;

	private static ArrayList<Snake> snakes;

	private AppFont font = AppLoader.loadFont("/fonts/vt323.ttf", AppFont.BOLD, 20);

	static Sound sonMouette;
	static Sound sonSncf;
	static Sound sonChute;
	static Sound sonCheval;
	static Sound sonEclair;
	static Sound sonMagic;
	static Sound sonMartien;
	static Sound sonPerdu;

	private Button replay,backMenu;
	private static Music soundMusicBackground;
	private static boolean jeuTermine = false;
	private Button config;

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
	public void init(final GameContainer container, final StateBasedGame game) {
		/* Méthode exécutée une unique fois au chargement du programme */
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
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		/* Méthode exécutée environ 60 fois par seconde */
		for(int i=0;i<bonus.size();i++){
			bonus.get(i).render(container, game, g);
		}

		for(int i=0;i<snakes.size();i++){
			snakes.get(i).render(container, game, g);
			g.setColor(Color.black);
		}

		g.setColor(new Color(150,150,150));
		g.fillRect(World.longueur-widthBandeau+2,0,widthBandeau,World.hauteur);
		g.setColor(new Color(170,170,170));
		g.fillRect(World.longueur-widthBandeau+4,0,widthBandeau,World.hauteur);
		g.setColor(new Color(200,200,200));
		g.fillRect(World.longueur-widthBandeau+6,0,widthBandeau,World.hauteur);

		g.setFont(font);
		g.setColor(Color.black);
		g.drawString("SNAKE 3000 ! ",World.longueur-widthBandeau+20,20);

		g.setColor(new Color(150,150,150));
		g.fillRect(World.longueur-widthBandeau+6,60,widthBandeau,5);
		g.resetFont();

		if(jeuTermine){
			g.setColor(Color.black);
			g.fillRoundRect(World.longueur/2-75,World.hauteur/2-50,150,100,20);
			g.setColor(Color.white);
			g.fillRoundRect(World.longueur/2-75+4,World.hauteur/2-50+4,150-8,92,20);
			g.setColor(Color.black);
			g.setFont(font);
			g.drawString("Perdu !", World.longueur/2-30,World.hauteur/2-30);
		}else{

		}

		for(int i=0;i<snakes.size();i++){
			g.setColor(snakes.get(i).couleur);
			g.drawString(snakes.get(i).nom+" : "+snakes.get(i).score,World.longueur-widthBandeau+20,100+50*i+20);
		}

		config.render(container, game, g);
		replay.render(container, game, g);
		backMenu.render(container, game, g);
		menu.render(container, game, g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		/* Méthode exécutée environ 60 fois par seconde */
		Input input = container.getInput();
		if (input.isKeyDown(Input.KEY_ESCAPE)) {
			this.setState(1);
			game.enterState(2, new FadeOutTransition(), new FadeInTransition());
		}
		menu.update(container, game, delta);
		replay.update(container, game,delta);
		backMenu.update(container, game,delta);
		config.update(container,game,delta);
		Collections.sort(snakes, new Comparator<Snake>() {
			@Override
			public int compare(Snake s1, Snake s2) {
				if(s1.score>s2.score)return -1;
				else if(s1.score<s2.score)return 1;
				return 0;
			}
		});

		if(jeuDemarre){

			if(!jeuTermine){
				jeuTermine = isFini();
				addBonus();

				for(int i=0;i<snakes.size();i++) {
					Snake snake = snakes.get(i);

					snake.GScore(1);
					snake.update(container, game, delta);

					for (int j = 0; j < bonus.size(); j++) {
						bonus.get(j).update(container, game, delta);
						if (!snakes.get(i).mort) {
							if (bonus.get(j).isInBonus(snakes.get(i).body.get(0))) {
								applyBonus(bonus.get(j), snakes.get(i));
								bonus.remove(j);
								j--;
							}
						}
					}

/*
					if(!snake.mort){
						if(collide(snake.body.get(0),snake,true)){
							snake.meurt();
						}
					}
*/

					for (int j = 0; j < snakes.size(); j++) {

						if (j != i) {
							if (!snakes.get(i).mort) {
								if (collide(snake.body.get(0), snakes.get(j),false)) {
									snake.meurt();
								}
							}
						}

					}
				}

			}
		}

	}

	public void play(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois au début du jeu */
		menu = new MenuMulti();
		menu.init(container, game);

		try {
		sonMouette = new Sound("sounds/snake3000/seagulls-chatting.ogg");
		sonSncf = new Sound("sounds/snake3000/0564.ogg");
		sonChute = new Sound("sounds/snake3000/0477.ogg");
		sonCheval = new Sound("sounds/snake3000/horse-whinnies.ogg");
		sonEclair = new Sound("sounds/snake3000/ChargedLightningAttack8-Bit.ogg");
		sonMagic = new Sound("sounds/snake3000/FreezeMagic.ogg");
		sonMartien = new Sound("sounds/snake3000/martian-gun.ogg");
		sonPerdu = new Sound("sounds/snake3000/perdu.ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}

		replay = new Button(container,World.longueur - widthBandeau+20, World.hauteur-150,widthBandeau-40,40);
		replay.setText("REJOUER");
		replay.setBackgroundColor(Color.black);
		replay.setBackgroundColorEntered(Color.white);
		replay.setTextColor(Color.white);
		replay.setTextColorEntered(Color.black);
		replay.setCornerRadius(25);
		replay.setOnClickListener(new TGDComponent.OnClickListener() {
			@Override
			public void onClick(TGDComponent componenent) {
				if(soundMusicBackground!=null)soundMusicBackground.stop();
				reset();
				menu.startGame();
			}
		});

		config = new Button(container,World.longueur - widthBandeau+20, World.hauteur-100,widthBandeau-40,40);
		config.setText("CONFIGURATION");
		config.setBackgroundColor(Color.black);
		config.setBackgroundColorEntered(Color.white);
		config.setTextColor(Color.white);
		config.setTextColorEntered(Color.black);
		config.setCornerRadius(25);
		config.setOnClickListener(new TGDComponent.OnClickListener() {
			@Override
			public void onClick(TGDComponent componenent) {
				if(soundMusicBackground!=null)soundMusicBackground.stop();
				menu = new MenuMulti();
				menu.init(container, game);
				reset();
			}
		});

		backMenu = new Button(container,World.longueur - widthBandeau+20, World.hauteur-50,widthBandeau-40,40);
		backMenu.setText("RETOUR AU MENU");
		backMenu.setBackgroundColor(Color.black);
		backMenu.setBackgroundColorEntered(Color.white);
		backMenu.setTextColor(Color.white);
		backMenu.setTextColorEntered(Color.black);
		backMenu.setCornerRadius(25);
		backMenu.setOnClickListener(new TGDComponent.OnClickListener() {
			@Override
			public void onClick(TGDComponent componenent) {
				if(soundMusicBackground!=null)soundMusicBackground.stop();
				game.enterState(1,new FadeOutTransition(),new FadeInTransition());
			}
		});

		reset();
	}

	public void pause(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la mise en pause du jeu */
	}

	public void resume(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée lors de la reprise du jeu */
	}

	public void stop(GameContainer container, StateBasedGame game) {
		/* Méthode exécutée une unique fois à la fin du jeu */
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return this.state;
	}

	private void applyBonus(Bonus bonus, Snake snake ) {
		bonus.applyBonus(snake);

		if(bonus.type == Bonus.bonusType.bInverseBonus){
			for(int i= 0;i<snakes.size();i++){
				if(!snakes.get(i).equals(snake)){
					snakes.get(i).inverse = !snakes.get(i).inverse;
				}

			}
		}
	}

	private boolean collide(Point point, Snake snake, boolean exceptHead) {
		//if(snake.invincible)return false;

		for(int i=exceptHead?3:0;i<snake.body.size();i++)
		{
			if(snake.body.get(i).x==point.x && snake.body.get(i).y==point.y){
				if(i==0)snakes.get(i).meurt();
				return true;
			}
		}
		return false;
	}

	public static void addBonus(Bonus bonusLoc)
	{
		bonus.add(bonusLoc);
	}

	private static void addBonus(){
		Random r = new Random();
		if(r.nextFloat() >= 0.99){
			bonus.add(Bonus.RandomBonus(new Point(r.nextInt(nbcasesl)-28,r.nextInt(nbcasesh))));
		}
	}

	@Override
	public void keyPressed(int key, char c){

		for(int i=0;i<snakes.size();i++){
			snakes.get(i).keyPressed(key,c);
		}
	}

	public static void reset() {
		snakes = new ArrayList<Snake>();
		bonus = new ArrayList<>();
		//menu.enleve = false;
		//menu.nJoueur = 0;
		jeuDemarre = false;
		jeuTermine = false;

	}

	public static void setSnakes(Snake[] snake){
		snakes = new ArrayList<Snake>(Arrays.asList(snake));

		try {
			soundMusicBackground=new Music("musics/snake3000/hymne_russe.ogg");
			soundMusicBackground.loop(1,0.3f);
			jeuDemarre = true;

		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public static void dead(Snake snake){
		//snakes.remove(snake);
	}

	private boolean isFini() {

		int compt = 0;

		if(snakes.size()==1){
			if(snakes.get(0).mort)return true;
			else return false;
		}

		for(int i=0;i<snakes.size();i++){
			if(!snakes.get(i).mort)compt++;
		}

		if(compt<=1)return true;

		return false;
	}
}
