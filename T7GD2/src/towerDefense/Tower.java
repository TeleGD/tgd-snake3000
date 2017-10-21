package towerDefense;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Tower {
	private double x,y;
	private double damage;
	private Enemy enemy;
	private double range;
	private Image sprite;
	private double attackSpeed;
	private double timer;
	private int type;
	private int level;
	
	public double getX() {
		return x;                             
	}
	
	public double getY() {
		return y;
	}
	
	public Tower(double x,double y,double damage,double attackSpeed,double range,int type) {
		this.x=x;
		this.y=y;
		this.damage=damage;
		this.attackSpeed=attackSpeed;
		this.range=range;
		this.level=1;
		try {
			sprite = new Image("images/TowerDefense/TowerType"+type+"Level1.png");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	public Tower(double x,double y,int type) {
		this.x=x;
		this.y=y;		
	}
	
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		arg2.drawImage(sprite, (float)x, (float)y);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		timer-=delta;
		if (ChooseEnemy() && this.timer==0) {
			World.projectiles.add(new Projectile(x,y,enemy,damage));
			timer=attackSpeed;                 // exemple si cadence=0,5 sec, attackSpeed=500 (delta=nb de ms entre 2 frames)
		}
	}
	
	private boolean ChooseEnemy() {            // renvoie vrai si un ennemi est � port�e
		for (Enemy e : World.enemies) {        // cherche dans la liste des ennemis tri�e par ordre d'appartition
			if (Math.sqrt(Math.pow(this.x-this.y,2)+Math.pow(e.getX()-e.getY(),2))<this.range) {
				this.enemy = e;
				return true;
			}
		}
		return false;
	}
	
	public void upgrade(int n) {               // n correspond au niveau
		if (n==2) {
			this.level+=1;
			this.damage+=1;
			try {
				sprite = new Image("images/TowerDefense/TowerType"+type+"Level1.png");
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
		if (n==3) {
			this.level+=1;
			this.damage+=1;
			try {
				sprite = new Image("images/TowerDefense/TowerType"+type+"Level"+level+".png");
			} catch (SlickException e) {
				e.printStackTrace();
			}
			this.attackSpeed+=1;
		}
		if (n==4) {
			this.level+=1;
			this.damage+=1;
			try {
				sprite = new Image("images/TowerDefense/TowerType"+type+"Level"+level+".png");
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
		if (n==4) {
			this.level+=1;
			this.damage+=1;
			try {
				sprite = new Image("images/TowerDefense/TowerType"+type+"Level"+level+".png");
			} catch (SlickException e) {
				e.printStackTrace();
			}
			this.attackSpeed+=1;
		}
	}
}
