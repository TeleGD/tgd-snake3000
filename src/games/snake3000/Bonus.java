package games.snake3000;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Bonus {

	private Point pt;

	public static enum bonusType {bGrandis,bRetrecis,bRapide,bLent,bMort,bInverseBonus,bInverseMalus,bRemis,bInvincible};

	public bonusType type;
	private Image imageBonus;
	private int rayon;

	private int timer=0;

	private int nextX=0;
	private int nextY=0;

	public static Bonus RandomBonus(Point pt){
		Random r = new Random();
		double b = r.nextFloat();
		bonusType bonus;
		if(b < 0.40)
			bonus = bonusType.bGrandis;
		else if(b < 0.50)
			bonus = bonusType.bRetrecis;
		else if(b < 0.65)
			bonus = bonusType.bRapide;
		else if(b < 0.75)
			bonus = bonusType.bLent;
		else if(b < 0.80)
			bonus = bonusType.bInverseBonus;
		else if(b < 0.90)
			bonus = bonusType.bInverseMalus;
		else if(b < 0.95)
			bonus = bonusType.bInvincible;
		else if(b < 0.98)
			bonus = bonusType.bMort;
		else
			bonus = bonusType.bRemis;

		return new Bonus(pt,bonus,r.nextInt(2)+1);
	}

	private void CreeRemi(Point pt,int nx, int ny){
		Bonus b = new Bonus(pt,bonusType.bMort,1);
		b.nextX = nx;
		b.nextY = ny;
		b.timer =300;
		World.addBonus(b);
	}

	private Bonus(Point pt,int numBonus,int rayon){
		this(pt,bonusType.values()[numBonus],rayon);
	}

	private Bonus(Point pt,bonusType bonus,int rayon){
		this.pt=pt;
		this.type = bonus;

		try{
			this.imageBonus = new Image(imagePath());
		}
		catch(Exception e){
			e.printStackTrace();
		}

		this.rayon = rayon;
	}

	public void applyBonus(Snake s){
		switch(this.type){
		case bGrandis:
			s.grandir();
			s.grandir();
			s.grandir();
			s.grandir();
			World.sonMartien.play();
			s.GScore(100);
			break;
		case bRetrecis:
			if(s.body.size() >= 1)
				s.retrecir();
			if(s.body.size() >= 1)
				s.retrecir();
			if(s.body.size() >= 1)
				s.retrecir();
			World.sonMagic.play();
			s.GScore(200);
			break;
		case bRapide:
			s.plusRapide();
			World.sonSncf.play();
			s.GScore(500);
			break;
		case bLent:
			s.plusLent();
			World.sonCheval.play();
			s.GScore(50);
			break;
		case bMort:
			s.meurt();
			s.GScore(1500);
			World.sonChute.play();
		break;
		case bInverseMalus:
			s.inverse = !s.inverse;
			World.sonEclair.play();
			s.GScore(250);
			break;
		case bRemis:
			World.sonPerdu.play();
			s.GScore(2000);
			s.invincible = 30;
			for(int i=-1;i<1;i++)
				for(int j=-1;j<2;j++)
					CreeRemi(new Point(pt.x+5*i,pt.y+5*j), i, j);
		break;
		case bInvincible:
			s.invincible = 300;
			s.GScore(100);
			World.sonMouette.play();
		break;
		}
	}

	private String imagePath(){
		String path = "images/snake3000/";
		switch(type){
		case bGrandis:
			path+="Grand";
			break;
		case bRetrecis:
			path+="Petit";
		break;
		case bRapide:
			path+="Lapin";
		break;
		case bLent:
			path+="Tortue";
		break;
		case bMort:
			path+="Remi";
		break;
		case bInverseBonus:
			path+="InverseBonus";
		break;
		case bInverseMalus:
			path+="InverseMalus";
		break;
		case bRemis:
			path+="clown";
		break;
		case bInvincible:
			path+="mouette";
		break;
		}

		return path+".png";
	}

	public Boolean isInBonus(Point p){
		return(this.pt.x-p.x <= rayon && p.x-this.pt.x <= rayon && this.pt.y - p.y <= rayon && p.y-this.pt.y <= rayon);
	}

	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		imageBonus.draw(pt.x*10-10*rayon,pt.y*10-10*rayon,10+20*rayon,10+20*rayon);
	}

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(type == bonusType.bMort){
			if(timer > 0){
				this.pt.x = (pt.x + nextX) %128;
				if (pt.x < 0)
					pt.x += 128;
				this.pt.y = (pt.y + nextY) %72;
				if(pt.y < 0)
					pt.y+= 72;

				timer--;
			}
		}
	}

}
