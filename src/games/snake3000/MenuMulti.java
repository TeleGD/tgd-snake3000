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
	private String[] valTouchesDefaut = {"A","Z","O","P","W","X","B","N","1","2","9","0","4","5","6","7","F","G"};
	private Color[] couleursDefaut =new Color[] {Color.white, Color.blue,Color.red,Color.green,Color.pink,Color.yellow,Color.cyan,Color.orange,Color.magenta};
	private Color[] couleursJoueurs = couleursDefaut;
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
		nJoueur=9;
		pas = container.getHeight()/20;
		debutNom = longueurJeu/2 - longueurMenu/10;

		nbrJoueurs = new TextField(container, debutdroiteansx, debuty+pas-5,longueurMenu/20, TGDComponent.AUTOMATIC);
		nbrJoueurs.setPlaceHolder("");
		nbrJoueurs.setHasFocus(true);
		nbrJoueurs.setText(""+nJoueur);
		nbrJoueurs.setOnlyFigures(true);
		nbrJoueurs.setMaxNumberOfLetter(1);
		nbrJoueurs.setEnterActionListener(new EnterActionListener() {

			@Override
			public void onEnterPressed() {
				createJoueurs(container);
			}});

		boutonNbJoueurs = new Button("OK",container,nbrJoueurs.getX()+5+nbrJoueurs.getWidth(),debuty+pas-5,TGDComponent.AUTOMATIC, nbrJoueurs.getHeight());
		boutonNbJoueurs.setBackgroundColor(new Color(255,255,255));
		boutonNbJoueurs.setTextColor(Color.black);
		boutonNbJoueurs.setVisible(true);
		boutonNbJoueurs.setPadding(7,10,7,10);
		boutonNbJoueurs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				createJoueurs(container);

			}});

		boutonStart = new Button("START",container,longueurJeu/2-longueurMenu/6,(height+hauteurMenu)/2-8*hauteurMenu/75,longueurMenu/3,hauteurMenu/15);
		boutonStart.setBackgroundColor(new Color(0,200,0));
		boutonStart.setVisible(true);
		boutonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				startGame((World) game.getState(5));
				game.enterState(5, new FadeOutTransition(), new FadeInTransition());
			}});

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
			TextField[] keys = this.touchesClavier;
			for (int i = 0; i < snakeCount; ++i) {
				Color color = colors[i];
				String name = names[i].getText();
				int leftKey = this.getInputValue(keys[i * 2].getText());
				int rightKey = this.getInputValue(keys[i * 2 + 1].getText());
				int posX = (i * 2 + 1) * columns / (snakeCount * 2);
				snakes[i] = new Snake(color, name, leftKey, rightKey, posX);
			}
			world.setSnakes(snakes);
		}
	}

	private void createJoueurs(GameContainer container) {
		if (nbrJoueurs.getText().length() ==1) {
			nJoueur = Integer.parseInt(nbrJoueurs.getText());
		}

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

					System.out.println("aller clic");
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
						}});
				}});

			touchesClavier[2*i] = new TextField(container,choixCouleur[i].getX()+choixCouleur[i].getWidth()+5,yn,hauteurMenu/15,hauteurMenu/15);
			touchesClavier[2*i].setText(valTouchesDefaut[2*i]);
			touchesClavier[2*i].setPlaceHolder("");
			touchesClavier[2*i].setUpperCaseLock(true);
			touchesClavier[2*i].setBackgroundColor(new Color(0,0,0));
			touchesClavier[2*i].setBackgroundColorEntered(new Color(255,255,255,100));
			touchesClavier[2*i].setBackgroundColorPressed(new Color(255,0,0,0));
			touchesClavier[2*i].setCursorEnabled(false);
			touchesClavier[2*i].setTextColor(Color.white);
			touchesClavier[2*i].setMaxNumberOfLetter(1);
			touchesClavier[2*i].setBackgroundColorFocused(new Color(255,0,0,0));
			touchesClavier[2*i].setOverflowMode(true);

			touchesClavier[2*i+1] = new TextField(container,touchesClavier[2*i].getX()+touchesClavier[2*i].getWidth()+5,yn,hauteurMenu/15,hauteurMenu/15);
			touchesClavier[2*i+1].setText(valTouchesDefaut[2*i+1]);
			touchesClavier[2*i+1].setPlaceHolder("");
			touchesClavier[2*i+1].setBackgroundColorEntered(new Color(255,255,255,100));
			touchesClavier[2*i+1].setBackgroundColorPressed(new Color(255,0,0,0));
			touchesClavier[2*i+1].setTextColor(Color.white);
			touchesClavier[2*i+1].setBackgroundColor(new Color(0,0,0));
			touchesClavier[2*i+1].setMaxNumberOfLetter(1);
			touchesClavier[2*i+1].setUpperCaseLock(true);
			touchesClavier[2*i+1].setCursorEnabled(false);
			touchesClavier[2*i+1].setBackgroundColorFocused(new Color(255,0,0,0));
			touchesClavier[2*i+1].setOverflowMode(true);

		}
	}

	public void update(GameContainer container, StateBasedGame game, int delta) {
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

	private int getInputValue(String s) {
		s=s.toLowerCase();
		if(s.equals("a"))return Input.KEY_A;
		if(s.equals("b"))return Input.KEY_B;
		if(s.equals("c"))return Input.KEY_C;
		if(s.equals("d"))return Input.KEY_D;
		if(s.equals("e"))return Input.KEY_E;
		if(s.equals("f"))return Input.KEY_F;
		if(s.equals("g"))return Input.KEY_G;
		if(s.equals("h"))return Input.KEY_H;
		if(s.equals("i"))return Input.KEY_I;
		if(s.equals("j"))return Input.KEY_J;
		if(s.equals("k"))return Input.KEY_K;
		if(s.equals("l"))return Input.KEY_L;
		if(s.equals("m"))return Input.KEY_M;
		if(s.equals("n"))return Input.KEY_N;
		if(s.equals("o"))return Input.KEY_O;
		if(s.equals("p"))return Input.KEY_P;
		if(s.equals("q"))return Input.KEY_Q;
		if(s.equals("r"))return Input.KEY_R;
		if(s.equals("s"))return Input.KEY_S;
		if(s.equals("t"))return Input.KEY_T;
		if(s.equals("u"))return Input.KEY_U;
		if(s.equals("v"))return Input.KEY_V;
		if(s.equals("w"))return Input.KEY_W;
		if(s.equals("x"))return Input.KEY_X;
		if(s.equals("y"))return Input.KEY_Y;
		if(s.equals("z"))return Input.KEY_Z;
		if(s.equals("0"))return Input.KEY_NUMPAD0;
		if(s.equals("1"))return Input.KEY_NUMPAD1;
		if(s.equals("2"))return Input.KEY_NUMPAD2;
		if(s.equals("3"))return Input.KEY_NUMPAD3;
		if(s.equals("4"))return Input.KEY_NUMPAD4;
		if(s.equals("5"))return Input.KEY_NUMPAD5;
		if(s.equals("6"))return Input.KEY_NUMPAD6;
		if(s.equals("7"))return Input.KEY_NUMPAD7;
		if(s.equals("8"))return Input.KEY_NUMPAD8;
		if(s.equals("9"))return Input.KEY_NUMPAD9;

		return 0;
	}

}
