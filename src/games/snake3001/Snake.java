package games.snake3001;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

import java.util.ArrayList;

public class Snake {
	private static int nbcasesh=72;
	private static int nbcasesl=128;
	private int horizontal= World.longueur/nbcasesl;
	private int vertical= World.hauteur/nbcasesh;
	public ArrayList<Point> body=new ArrayList<Point>();
	public Color couleur;
	private int TDroite;
	private int TGauche;
	protected boolean rightPress,leftPress;
	public String nom;
	private int speed;
	private int dir;
	public int score;
	public boolean mort ;
	public boolean inverse;
	public int invincible;
	public String ipAdress;

	public Snake(Color couleur,int x_init, int TDroite, int TGauche,int taille_init, String nom,int speed) {
		this.couleur = couleur;
		this.dir = 0;
		this.TDroite = TDroite;
		this.TGauche = TGauche;
		this.body = new ArrayList<Point>();
		this.nom = nom;
		this.score = 0;
		this.speed = speed;
		this.mort = false;
		this.inverse = false;
		this.invincible = 0;
		for (int i = 0;i<taille_init;i++){
			body.add(new Point(x_init,(nbcasesh-i)));
		}
	}

	public void GScore(int x) {

		if (mort==false){
		score += x;
		}
		}

	public void meurt(){
		mort = true;
	}

	private void move() {
		if (body.size()!=0){

		Point ajout = null;
		if (dir == 0) { //haut
			if (body.get(0).y > 0) {
				ajout = new Point((body.get(0).x) , (body.get(0).y - 1));
				}
			else {
				ajout = new Point((body.get(0).x) , nbcasesh-1);}
		}
		if (dir == 1) { //droite
			if (body.get(0).x < nbcasesl-29 ) {
				ajout = new Point((body.get(0).x + 1) , (body.get(0).y));
			}else {
				ajout = new Point(0 , body.get(0).y) ;
				}
		}
		if (dir == 2) { //bas
			if (body.get(0).y < nbcasesh-1) {
				ajout = new Point((body.get(0).x) , (body.get(0).y + 1));}
			else {
				ajout = new Point((body.get(0).x) , 0);
				}
		}
		if (dir == 3) { //gauche
			if (body.get(0).x > 0) {
				ajout = new Point((body.get(0).x-1) , (body.get(0).y));
				}
			else {
				ajout = new Point(nbcasesl-29 , (body.get(0).y));}
		}
		body.remove((body.size()-1));
		if (body.size()==0) {
			World.dead(this);
		}
		if (mort == false){
			body.add(0,ajout);
		}}
	}

	/*private void turn() {
		if (rightPress) {
			dir = 1 ;
			rightPress=false;
		}
		if (leftPress) {
			dir = dir -1;
			leftPress=false;
		}
	}*/

	public void keyReleased(int key, char c) {

		switch (key) {
		case Input.KEY_RIGHT:
			rightPress = false;
			break;

		}
		switch (key) {
		case Input.KEY_LEFT:
			leftPress = false;
			break;

		}
	}

	public void keyPressed(int key, char c) {
		if ((key == TDroite && !inverse) || (key == TGauche && inverse)) {
			rightPress = true;
			dir += 1;
			dir = dir % 4;
		}
		if ((key == TDroite && inverse) || (key == TGauche && !inverse)) {
			leftPress = true;
			dir -= 1;
			dir += 4;
			dir = dir % 4;

		}
	}

	public void grandir(){
		Point ajout = null;
		if (dir == 0) {
			if (body.get(0).y != 0) {
				ajout = new Point((body.get(0).x) , (body.get(0).y - 1));
				}
			else {
				ajout = new Point((body.get(0).x) , nbcasesh);}
		}
		if (dir == 1) {
			if (body.get(0).x != nbcasesl-28 ) {
				ajout = new Point((body.get(0).x + 1) , (body.get(0).y));
			}else {
				ajout = new Point(0 , body.get(0).y) ;
				}
		}
		if (dir == 2) {
			if (body.get(0).y != nbcasesh) {
				ajout = new Point((body.get(0).x) , (body.get(0).y + 1));}
			else {
				ajout = new Point((body.get(0).x) , 0);
				}
		}
		if (dir == 3) {
			if (body.get(0).x != 0) {
				ajout = new Point((body.get(0).x-1) , (body.get(0).y));
				}
			else {
				ajout = new Point(nbcasesl-28 , (body.get(0).y));}
		}
		body.add(0,ajout);
	}

	public void retrecir(){
		if((body.size() == 1))
			this.meurt();
		body.remove((body.size()-1));
	}

	public void plusRapide(){
		speed += 7;
	}

	public void plusLent(){
		if (speed > 4){
		speed -= 4;
		}
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		for (int i = 0 ; i<body.size(); i++) {
			g.setColor(couleur);
			g.fillRect(body.get(i).x*horizontal,body.get(i).y*vertical,horizontal,vertical);
		}
	}
	int compteur = 0;
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (invincible >0) {
			if(invincible % 40 > 20)
				couleur= new Color(couleur.r,couleur.g,couleur.b,0.5f);
			else
				couleur= new Color(couleur.r,couleur.g,couleur.b,1);

			invincible = invincible - 1;
			}

		compteur += speed;
		while(compteur >= 15){
			move();
			compteur -=15;
		}
			//turn();
	}

}
