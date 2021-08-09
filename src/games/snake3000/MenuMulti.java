package games.snake3000;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import app.AppFont;
import app.AppLoader;
import app.ui.Button;
import app.ui.ColorPicker;
import app.ui.TextField;
import app.ui.TextField.EnterActionListener;
import app.ui.TGDComponent;
import app.ui.TGDComponent.OnClickListener;

public class MenuMulti extends BasicGameState {

	private int longueurJeu;
	private int hauteurMenu;
	private int longueurMenu;
	private int debutx;
	private int debuty;
	private TextField nbrJoueurs;
	private int nJoueur;
	private int pas;
	private TextField[] fieldNomsJoueurs;
	private TextField[] touchesClavier;
	private int debutNom;

	private Button boutonStart,boutonNbJoueurs;
	private int[] touchesDefaut = new int[]{Input.KEY_A,Input.KEY_Q,Input.KEY_L,Input.KEY_P,Input.KEY_F,Input.KEY_V,Input.KEY_B,Input.KEY_H};
	private int[] touchesJoueurs = touchesDefaut.clone();
	private Color[] couleursDefaut = new Color[]{Color.red,Color.orange,Color.green,Color.blue,Color.pink,Color.yellow,Color.cyan,Color.magenta};
	private Color[] couleursJoueurs = couleursDefaut.clone();
	private Button[] choixCouleur;
	private ColorPicker picker;
	private AppFont fontTitle = AppLoader.loadFont("/fonts/vt323.ttf", AppFont.BOLD, 25);
	private AppFont fontNbJoueurs = AppLoader.loadFont("/fonts/vt323.ttf", AppFont.BOLD, 20);

	private int ID;

	public MenuMulti(int ID) {
		this.ID = ID;
	}

	@Override
	public int getID() {
		return this.ID;
	}

	public void init(GameContainer container, StateBasedGame game) {
		int width = container.getWidth();
		int height = container.getHeight();
		longueurJeu=(int)(container.getWidth()*.8);

		hauteurMenu=(int)(container.getHeight()/1.45);
		longueurMenu=container.getWidth()/2;
		debutx=(longueurJeu-longueurMenu)/2+longueurMenu/15;
		debuty=(container.getHeight()-hauteurMenu)/2+hauteurMenu/15;
		int debutdroiteansx=(longueurJeu+longueurMenu)/2-longueurMenu/10-longueurMenu/8;
		nJoueur=4;
		pas = container.getHeight()/20;
		debutNom = longueurJeu/2 - longueurMenu/10;

		nbrJoueurs = new TextField(container, debutdroiteansx, debuty+pas-5,longueurMenu/20, TGDComponent.AUTOMATIC) {

			@Override
			public void keyPressed(int key, char c) {
				String oldText = super.getText();
				super.keyPressed(key, c);
				String newText = super.getText();
				if (newText.length() == 0) {
					super.setText("0");
				} else if (!newText.equals(oldText) && Integer.parseInt(newText) > 4) {
					super.setText(oldText);
				}
			}

		};
		nbrJoueurs.setHasFocus(true);
		nbrJoueurs.setText(""+nJoueur);
		nbrJoueurs.setBackgroundColor(new Color(0,0,0));
		nbrJoueurs.setBackgroundColorEntered(new Color(255,255,255,100));
		nbrJoueurs.setBackgroundColorPressed(new Color(255,0,0,0));
		nbrJoueurs.setCursorEnabled(false);
		nbrJoueurs.setTextColor(new Color(255,255,255));
		nbrJoueurs.setBackgroundColorFocused(new Color(255,0,0,0));
		nbrJoueurs.setOnlyFigures(true);
		nbrJoueurs.setMaxNumberOfLetter(1);
		nbrJoueurs.setOverflowMode(true);
		nbrJoueurs.setEnterActionListener(new EnterActionListener() {

			@Override
			public void onEnterPressed() {
				createJoueurs(container);
			}

		});

		boutonNbJoueurs = new Button("OK",container,nbrJoueurs.getX()+5+nbrJoueurs.getWidth(),debuty+pas-5,TGDComponent.AUTOMATIC, nbrJoueurs.getHeight());
		boutonNbJoueurs.setBackgroundColor(new Color(255,255,255));
		boutonNbJoueurs.setTextColor(Color.black);
		boutonNbJoueurs.setVisible(true);
		boutonNbJoueurs.setPadding(7,10,7,10);
		boutonNbJoueurs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				createJoueurs(container);
			}

		});

		boutonStart = new Button("START",container,longueurJeu/2-longueurMenu/6,(height+hauteurMenu)/2-8*hauteurMenu/75,longueurMenu/3,hauteurMenu/15);
		boutonStart.setBackgroundColor(new Color(0,200,0));
		boutonStart.setVisible(true);
		boutonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				startGame((World) game.getState(5));
				game.enterState(5, new FadeOutTransition(), new FadeInTransition());
			}

		});

		picker = new ColorPicker(container,debutx,0,width/5,height/4);
		picker.setVisible(false);
		createJoueurs(container);
	}

	public void startGame(World world) {
		int snakeCount = this.nJoueur;
		if (snakeCount != 0) {
			int columns = World.getColumns();
			Snake[] snakes = new Snake[nJoueur];
			Color[] colors = this.couleursJoueurs;
			TextField[] names = this.fieldNomsJoueurs;
			int[] keys = this.touchesJoueurs;
			for (int i = 0; i < snakeCount; ++i) {
				Color color = colors[i];
				String name = names[i].getText();
				int leftKey = keys[i * 2];
				int rightKey = keys[i * 2 + 1];
				int posX = (i * 2 + 1) * columns / (snakeCount * 2);
				snakes[i] = new Snake(color, name, leftKey, rightKey, posX);
			}
			world.setSnakes(snakes);
		}
	}

	private void createJoueurs(GameContainer container) {
		nJoueur = Integer.parseInt(nbrJoueurs.getText());
		fieldNomsJoueurs=new TextField[nJoueur];
		choixCouleur = new Button[nJoueur];
		touchesClavier = new TextField[nJoueur*2];
		for (int i = 0;i<nJoueur;i+=1) {
			int yn = debuty + (i+2)*pas+10;
			fieldNomsJoueurs[i] = new TextField(container , debutNom , yn , longueurMenu/3 , TGDComponent.AUTOMATIC );
			fieldNomsJoueurs[i].setBackgroundColor(Color.black);
			fieldNomsJoueurs[i].setTextColor(couleursJoueurs[i]);
			fieldNomsJoueurs[i].setText("Joueur "+(i+1));
			fieldNomsJoueurs[i].setPlaceHolder("Entrer le nom du joueur");
			fieldNomsJoueurs[i].setMaxNumberOfLetter(20);
			final int h=i;
			choixCouleur[i] = new Button(container,longueurJeu/2+longueurMenu/4,yn,hauteurMenu/15,hauteurMenu/15);
			choixCouleur[i].setBackgroundColor(couleursJoueurs[i]);
			choixCouleur[i].setBackgroundColorEntered(new Color(255,255,255,100));
			choixCouleur[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(TGDComponent componenent) {
					if (picker.getVisible()) {
						couleursJoueurs[h]=couleursDefaut[h];
						choixCouleur[h].setBackgroundColor(couleursDefaut[h]);
						fieldNomsJoueurs[h].setTextColor(couleursDefaut[h]);
						picker.setVisible(false);
						return;
					}
					picker.setY(choixCouleur[h].getY());
					picker.setColorSelected(couleursJoueurs[h]);
					picker.setX(longueurJeu/2+longueurMenu/3);
					picker.setVisible(true);
					picker.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(TGDComponent componenent) {
							couleursJoueurs[h]=picker.getColorSelected();
							choixCouleur[h].setBackgroundColor(picker.getColorSelected());
							fieldNomsJoueurs[h].setTextColor(picker.getColorSelected());
							picker.setVisible(false);
						}

					});
				}

			});
			for (int j = 0; j < 2; ++j) {
				int index = 2 * i + j;
				touchesClavier[index] = new TextField(container,j == 0 ? choixCouleur[i].getX()+choixCouleur[i].getWidth()+5 : touchesClavier[2*i].getX()+touchesClavier[2*i].getWidth()+5,yn,hauteurMenu/15,hauteurMenu/15) {

					@Override
					public void keyPressed(int key, char c) {
						String oldText = super.getText();
						super.keyPressed(key, c);
						String newText = super.getText();
						if (newText.length() == 0) {
							key = touchesDefaut[index];
							super.setText(Input.getKeyName(key));
						} else if (!newText.equals(oldText)) {
							if (c >= 97 && c < 123) {
								touchesJoueurs[index] = key;
								super.setText(Character.toString(c).toUpperCase());
							} else {
								super.setText(oldText);
							}
						}
					}

				};
				touchesClavier[index].setText(Input.getKeyName(touchesDefaut[index]));
				touchesClavier[index].setBackgroundColor(new Color(0,0,0));
				touchesClavier[index].setBackgroundColorEntered(new Color(255,255,255,100));
				touchesClavier[index].setBackgroundColorPressed(new Color(255,0,0,0));
				touchesClavier[index].setCursorEnabled(false);
				touchesClavier[index].setTextColor(new Color(255,255,255));
				touchesClavier[index].setBackgroundColorFocused(new Color(255,0,0,0));
				touchesClavier[index].setMaxNumberOfLetter(1);
				touchesClavier[index].setOverflowMode(true);
			}
		}
	}

	public void update(GameContainer container, StateBasedGame game, int delta) {
		Input input = container.getInput();
		if (input.isKeyDown(Input.KEY_ESCAPE)) {
			game.enterState(1, new FadeOutTransition(), new FadeInTransition());
		}
		nbrJoueurs.update(container, game, delta);
		boutonStart.update(container, game, delta);
		boutonNbJoueurs.update(container, game, delta);
		picker.update(container, game, delta);
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) {
			int height = container.getHeight();
			g.setColor(new Color(255,255,255));
			g.fillRect((longueurJeu-longueurMenu)/2-4, (height-hauteurMenu)/2-4, longueurMenu+8, hauteurMenu+9);
			g.setColor(new Color(100,100,200));
			g.fillRect((longueurJeu-longueurMenu)/2, (height-hauteurMenu)/2, longueurMenu, hauteurMenu);
			g.setColor(new Color(255,255,255));
			g.setFont(fontTitle);
			g.drawString("Configuration", longueurJeu/2-g.getFont().getWidth("Configuration")/2, debuty-pas/2);
			g.setFont(fontNbJoueurs);

			g.setColor(new Color(0,0,0));
			g.drawString("Nombre de joueurs : ", debutx, debuty+pas);
			nbrJoueurs.render(container, game, g);
			g.resetFont();
			for (int i = 1;i<=nJoueur;i+=1) {
				int yn = debuty + (i+1)*pas+10;
				g.setColor(new Color(0,0,0));
				if (fieldNomsJoueurs[i-1]!=null) {
					g.drawString("Nom Joueur "+i+" :",debutx,yn+5);
					//g.drawString(valTouchesDefaut[2*i-1]+" - "+valTouchesDefaut[2*i-2], longueurJeu/2+longueurMenu/3, yn+5);
					fieldNomsJoueurs[i-1].render(container, game, g);
					choixCouleur[i-1].render(container, game, g);
					touchesClavier[2*(i-1)].render(container, game, g);
					touchesClavier[2*(i-1)+1].render(container, game, g);
					picker.render(container, game, g);

				}

			}

			boutonStart.render(container, game, g);
			boutonNbJoueurs.render(container, game, g);
	}

}
