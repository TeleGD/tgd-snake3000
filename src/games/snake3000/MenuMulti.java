package games.snake3000;

import general.utils.FontUtils;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import general.ui.Button;
import general.ui.ColorPicker;
import general.ui.TGDComponent;
import general.ui.TGDComponent.OnClickListener;
import general.ui.TextField;
import general.ui.TextField.EnterActionListener;

public class MenuMulti {

	private int longueurJeu=(int)(World.longueur*0.8);

	private int hauteurMenu=(int)(World.hauteur/1.45);
	private int longueurMenu=World.longueur/2;
	private int debutx=(longueurJeu-longueurMenu)/2+longueurMenu/15;
	private int debuty=(World.hauteur-hauteurMenu)/2+hauteurMenu/15;
	private int debutdroiteansx=(longueurJeu+longueurMenu)/2-longueurMenu/10-longueurMenu/8;
	private TextField nbrJoueurs;
	private int nJoueur=9;
	private int pas = World.hauteur/20;
	private int yn;
	private TextField[] fieldNomsJoueurs;
	private TextField[] touchesClavier;

	private int debutNom = longueurJeu/2 - longueurMenu/10;
	private Button boutonStart,boutonNbJoueurs;
	private Snake[] joueurs;
	private boolean enleve=false;
	private String[] valTouchesDefaut = {"<-","->","A","Z","O","P","W","X","B","N","1","2","8","9","Y","U","F","G"};
	private Color[] couleursDefaut =new Color[] {Color.white, Color.blue,Color.red,Color.green,Color.pink,Color.yellow,Color.cyan,Color.orange,Color.magenta};
	private Color[] couleursJoueurs = couleursDefaut;
	private Button[] choixCouleur;
	private ColorPicker picker;
	private TrueTypeFont fontTitle = FontUtils.loadSystemFont("Arial", java.awt.Font.BOLD,25);
	private TrueTypeFont fontNbJoueurs = FontUtils.loadSystemFont("Arial", java.awt.Font.BOLD,20);

	public void init(final GameContainer container, StateBasedGame game) throws SlickException {
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

		boutonStart = new Button("START",container,longueurJeu/2-longueurMenu/6,(World.hauteur+hauteurMenu)/2-8*hauteurMenu/75,longueurMenu/3,hauteurMenu/15);
		boutonStart.setBackgroundColor(new Color(0,200,0));
		boutonStart.setVisible(true);
		boutonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				startGame();
			}});

		picker = new ColorPicker(container,debutx,0,World.longueur/5,World.hauteur/4);
		picker.setVisible(false);
		createJoueurs(container);
	}

	public void startGame() {
		if (nJoueur!=0) {
			// TODO Auto-generated method stub
			joueurs = new Snake[nJoueur];
			for (int i = 0;i<nJoueur;i+=1) {
				joueurs[i] = new Snake(couleursJoueurs[i],(100-nJoueur)/(nJoueur+1) + i*((100-nJoueur)/(nJoueur+1)+1),getInputValue(touchesClavier[2*i+1].getText()),getInputValue(touchesClavier[2*i].getText()),10,fieldNomsJoueurs[i].getText(),10);
			}
			World.setSnakes(joueurs);
			enleve = true;
		}
		boutonStart.setVisible(false);
	}

	private void createJoueurs(GameContainer container) {
		if (nbrJoueurs.getText().length() ==1) {
			nJoueur = Integer.parseInt(nbrJoueurs.getText());
		}

		fieldNomsJoueurs=new TextField[nJoueur];
		choixCouleur = new Button[nJoueur];
		touchesClavier = new TextField[nJoueur*2];

		for (int i = 0;i<nJoueur;i+=1) {
			yn = debuty + (i+2)*pas+10;
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
			touchesClavier[2*i].removeUnauthorizedKey(Input.KEY_LEFT,Input.KEY_RIGHT,Input.KEY_UP,Input.KEY_DOWN);

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
			touchesClavier[2*i+1].removeUnauthorizedKey(Input.KEY_LEFT,Input.KEY_RIGHT,Input.KEY_UP,Input.KEY_DOWN);

		}
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if (!enleve) {
			g.setColor(new Color(255,255,255));
			g.fillRect((longueurJeu-longueurMenu)/2-4, (World.hauteur-hauteurMenu)/2-4, longueurMenu+8, hauteurMenu+9);
			g.setColor(new Color(100,100,200));
			g.fillRect((longueurJeu-longueurMenu)/2, (World.hauteur-hauteurMenu)/2, longueurMenu, hauteurMenu);
			g.setColor(new Color(255,255,255));
			g.setFont(fontTitle);
			g.drawString("Configuration", longueurJeu/2-g.getFont().getWidth("Configuration")/2, debuty-pas/2);
			g.setFont(fontNbJoueurs);

			g.setColor(new Color(0,0,0));
			g.drawString("Nombre de joueurs : ", debutx, debuty+pas);
			nbrJoueurs.render(container, game, g);
			g.resetFont();
			for (int i = 1;i<=nJoueur;i+=1) {
				yn = debuty + (i+1)*pas+10;
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

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		nbrJoueurs.update(container, game, delta);
		boutonStart.update(container, game, delta);
		boutonNbJoueurs.update(container, game, delta);
		picker.update(container, game, delta);
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
		if(s.equals("<-"))return Input.KEY_LEFT;
		if(s.equals("->"))return Input.KEY_RIGHT;
		if(s.equals("<"))return Input.KEY_LEFT;
		if(s.equals(">"))return Input.KEY_RIGHT;
		if(s.equals("|"))return Input.KEY_UP;
		if(s.equals("!"))return Input.KEY_DOWN;

		return 0;
	}

}
