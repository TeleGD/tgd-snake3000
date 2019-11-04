package games.snake3001;

import general.ui.Button;
import general.ui.ColorPicker;
import general.ui.TGDComponent;
import general.ui.TGDComponent.OnClickListener;
import general.ui.TextField;
import general.ui.TextField.EnterActionListener;
import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

public class MenuMulti {

	private int longueurJeu=(int)(World.longueur*0.8);

	public int hauteurMenu=(int)(World.hauteur/1.5);
	public int longueurMenu= World.longueur/2;
	public int debutx=(longueurJeu-longueurMenu)/2+longueurMenu/15;
	public int debuty=(World.hauteur-hauteurMenu)/2+hauteurMenu/10;
	public int debutdroiteansx=(longueurJeu+longueurMenu)/2-longueurMenu/10-longueurMenu/8;
	public TextField nbrJoueurs;
	public int nJoueur=0;
	public int pas = World.hauteur/20;
	public int yn;
	public String[] nomsJoueurs;
	public TextField[] fieldNomsJoueurs;
	public int debutNom = longueurJeu/2 - longueurMenu/10;
	private Button boutonStart;
	private Snake[] joueurs;
	public boolean enleve=false;
	private int[] touchesDefaut ={Input.KEY_LEFT,Input.KEY_RIGHT, Input.KEY_A, Input.KEY_Z, Input.KEY_O, Input.KEY_P, Input.KEY_W, Input.KEY_X, Input.KEY_B, Input.KEY_N, Input.KEY_NUMPAD1,Input.KEY_NUMPAD2,Input.KEY_NUMPAD8,Input.KEY_NUMPAD9,Input.KEY_Y,Input.KEY_U,Input.KEY_F,Input.KEY_G};
	private String[] valTouchesDefaut = {"<-","->","A","Z","O","P","W","X","B","N","1","2","8","9","Y","U","F","G"};
	private Color[] couleursDefaut =new Color[] {Color.white, Color.blue,Color.red,Color.green,Color.pink,Color.yellow,Color.cyan,Color.orange,Color.magenta};
	private Color[] couleursJoueurs = couleursDefaut;
	private Button[] choixCouleur;
	private ColorPicker picker;
	private boolean affPicker=false;

	public MenuMulti() {

	}

	public void init(final GameContainer container, StateBasedGame game) throws SlickException {
		nbrJoueurs = new TextField(container, debutdroiteansx, debuty,longueurMenu/8, hauteurMenu/15);
		nbrJoueurs.setPlaceHolder("");
		nbrJoueurs.setHasFocus(true);
		nbrJoueurs.setText("0");
		nbrJoueurs.setPadding(5, 5, 0, 23);
		nbrJoueurs.setOnlyFigures(true);
		nbrJoueurs.setMaxNumberOfLetter(1);
		picker = new ColorPicker(container,0,0, World.longueur/5, World.hauteur/4);
		nbrJoueurs.setEnterActionListener(new EnterActionListener() {

			@Override
			public void onEnterPressed() {
				if (nbrJoueurs.getText().length() ==1) {
					nJoueur = Integer.parseInt(nbrJoueurs.getText());
				}
				nomsJoueurs=new String[nJoueur];
				fieldNomsJoueurs=new TextField[nJoueur];
				choixCouleur = new Button[nJoueur];
				for (int i = 0;i<nJoueur;i+=1) {
					yn = debuty + (i+1)*pas;
					fieldNomsJoueurs[i] = new TextField(container , debutNom , yn , longueurMenu/3 , hauteurMenu/15 );
					fieldNomsJoueurs[i].setBackgroundColor(Color.black);
					fieldNomsJoueurs[i].setTextColor(couleursJoueurs[i]);
					fieldNomsJoueurs[i].setText("Joueur "+(i+1));
					fieldNomsJoueurs[i].setPlaceHolder("Entrer le nom du joueur");
					fieldNomsJoueurs[i].setPadding(5, 5, 0, 15);
					fieldNomsJoueurs[i].setOnlyFigures(false);
					fieldNomsJoueurs[i].setMaxNumberOfLetter(20);
					final int h=i;
					choixCouleur[i] = new Button(container,longueurJeu/2+longueurMenu/4,yn,hauteurMenu/15,hauteurMenu/15);
					choixCouleur[i].setBackgroundColor(couleursJoueurs[i]);
					choixCouleur[i].setBackgroundColorEntered(new Color(255,255,255,100));
					choixCouleur[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(TGDComponent componenent) {
							// TODO Auto-generated method stub
							picker.setY(choixCouleur[h].getY());
							picker.setColorSelected(couleursJoueurs[h]);
							picker.setX(longueurJeu/2+longueurMenu/3);
							affPicker = true;
							picker.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(TGDComponent componenent) {
									// TODO Auto-generated method stub
									System.out.println("salut");
									couleursJoueurs[h]=picker.getColorSelected();
									choixCouleur[h].setBackgroundColor(picker.getColorSelected());
									fieldNomsJoueurs[h].setTextColor(picker.getColorSelected());
									affPicker = false;
								}});
						}});
				}
			}});


		boutonStart = new Button("START",container,longueurJeu/2-longueurMenu/6,(World.hauteur+hauteurMenu)/2-8*hauteurMenu/75,longueurMenu/3,hauteurMenu/15);
		boutonStart.setBackgroundColor(Color.green);
		boutonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(TGDComponent componenent) {
				if (nJoueur!=0) {
					// TODO Auto-generated method stub
					joueurs = new Snake[nJoueur];
					for (int i = 0;i<nJoueur;i+=1) {
						joueurs[i] = new Snake(couleursJoueurs[i],(100-nJoueur)/(nJoueur+1) + i*((100-nJoueur)/(nJoueur+1)+1),touchesDefaut[2*i],touchesDefaut[2*i+1],10,fieldNomsJoueurs[i].getText(),10);
					}
					World.setSnakes(joueurs);
					enleve = true;
				}
			}});


	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if (!enleve) {
			g.setColor(new Color(0,0,255));
			g.fillRect((longueurJeu-longueurMenu)/2, (World.hauteur-hauteurMenu)/2, longueurMenu, hauteurMenu);
			g.setColor(new Color(0,0,0));
			g.drawString("nombre de joueurs : ", debutx, debuty);
			nbrJoueurs.render(container, game, g);

			for (int i = 1;i<=nJoueur;i+=1) {
				yn = debuty + i*pas;
				g.setColor(new Color(0,0,0));
				if (fieldNomsJoueurs[i-1]!=null) {
					g.drawString("Nom Joueur n°"+i+" :",debutx,yn+5);
					g.drawString(valTouchesDefaut[2*i-1]+" - "+valTouchesDefaut[2*i-2], longueurJeu/2+longueurMenu/3, yn+5);
					fieldNomsJoueurs[i-1].render(container, game, g);
					choixCouleur[i-1].render(container, game, g);
					if (affPicker) {
						picker.render(container, game, g);
					}
				}


			}

			boutonStart.render(container, game, g);
		}
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		nbrJoueurs.update(container, game, delta);
		boutonStart.update(container, game, delta);

	}

}
