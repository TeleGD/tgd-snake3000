package games.snake3000;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.state.StateBasedGame;

import app.AppLoader;

public class Bonus {

	public static Bonus createRandomBonus(Random random) {
		BonusType type;
		double b = random.nextFloat();
		// 1f - (9f - n) * (9f - n) / 100f pour n allant de 0f à 8f
		if (b < .19f) {
			type = BonusType.shrink;
		} else if (b < .36f) {
			type = BonusType.expand;
		} else if (b < .51f) {
			type = BonusType.decelerate;
		} else if (b < .64f) {
			type = BonusType.accelerate;
		} else if (b < .75f) {
			type = BonusType.reverseSelf;
		} else if (b < .84f) {
			type = BonusType.reverseOthers;
		} else if (b < .91f) {
			type = BonusType.kill;
		} else if (b < .96f) {
			type = BonusType.boost;
		} else if (b < .99f) {
			type = BonusType.surprise;
		} else {
			type = BonusType.trap;
		}
		boolean big = random.nextBoolean();
		int posX = random.nextInt(World.getColumns());
		int posY = random.nextInt(World.getRows());
		return new Bonus(type, big, posX, posY);
	}

	private BonusType type;
	private boolean big;
	private int posX;
	private int posY;
	private int dirX;
	private int dirY;
	private int timer;
	private boolean consumed;
	private Image image;
	private Audio sound;

	private Bonus(BonusType type, boolean big, int posX, int posY) {
		int columns = World.getColumns();
		int rows = World.getRows();
		this.type = type;
		this.big = big;
		this.posX = (posX % columns + columns) % columns;
		this.posY = (posY % rows + rows) % rows;
		this.dirX = 0;
		this.dirY = 0;
		this.timer = 0;
		this.consumed = false;
		this.image = AppLoader.loadPicture("/images/snake3000/" + type + ".png");
		this.sound = AppLoader.loadAudio("/sounds/snake3000/" + type + ".ogg");
	}

	public void update(GameContainer container, StateBasedGame game, int delta) {
		if (this.isConsumed()) {
			return;
		}
		if (timer > 0) {
			int columns = World.getColumns();
			int rows = World.getRows();
			this.posX = ((this.posX + this.dirX) % columns + columns) % columns;
			this.posY = ((this.posY + this.dirY) % rows + rows) % rows;
			timer = Math.max(timer - delta, 0);
		}
	}

	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		if (this.isConsumed()) {
			return;
		}
		int caseSize = World.getCaseSize();
		int gridWidth = World.getColumns() * caseSize;
		int gridHeight = World.getRows() * caseSize;
		int size = this.getSize() * caseSize;
		int posX = this.posX * caseSize;
		int posY = this.posY * caseSize;
		Image image = this.image;
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		for (int i = -1; i < 1; ++i) {
			int x0 = posX + gridWidth * i;
			int x1 = x0 + size;
			for (int j = -1; j < 1; ++j) {
				int y0 = posY + gridHeight * j;
				int y1 = y0 + size;
				context.drawImage(image, x0, y0, x1, y1, 0, 0, imageWidth, imageHeight);
			}
		}
	}

	public boolean isConsumed() {
		return this.consumed;
	}

	public void apply(World world, Snake snake) {
		if (this.consumed) {
			return;
		}
		this.consumed = true;
		this.sound.playAsSoundEffect(1, .3f, false);
		int score = 0;
		// termes 3, 2, 5, 4, 7, 6, 9, 8, 11, 10 de la suite de Fibonacci
		switch (this.type) {
			case shrink: {
				for (int i = 0; i < 3; ++i) {
					snake.shrink();
				}
				score = 2000;
				break;
			}
			case expand: {
				for (int i = 0; i < 4; ++i) {
					snake.expand();
				}
				score = 1000;
				break;
			}
			case decelerate: {
				snake.decelerate();
				score = 5000;
				break;
			}
			case accelerate: {
				snake.accelerate();
				score = 3000;
				break;
			}
			case reverseSelf: {
				snake.reverse();
				score = 13000;
				break;
			}
			case reverseOthers: {
				for (Snake otherSnake: world.getSnakes()) {
					if (otherSnake == snake) {
						continue;
					}
					otherSnake.reverse();
				}
				score = 8000;
				break;
			}
			case kill: {
				snake.kill();
				score = 34000;
				break;
			}
			case boost: {
				snake.boost(5000);
				score = 21000;
				break;
			}
			case surprise: {
				BonusType type = BonusType.boost;
				boolean big = this.big;
				int size = this.getSize();
				int posX = this.posX;
				int posY = this.posY;
				for (int i = 0; i < 2; ++i) {
					int dirX = i * 2 - 1;
					for (int j = 0; j < 2; ++j) {
						int dirY = j * 2 - 1;
						Bonus bonus = new Bonus(type, big, posX + size * dirX, posY + size * dirY);
						bonus.dirX = dirX;
						bonus.dirY = dirY;
						bonus.timer = 2000;
						world.addBonus(bonus);
					}
				}
				score = 89000;
				break;
			}
			case trap: {
				snake.boost(1000);
				BonusType type = BonusType.kill;
				boolean big = this.big;
				int size = this.getSize();
				int posX = this.posX;
				int posY = this.posY;
				for (int i = -1; i < 2; ++i) {
					for (int j = -1; j < 2; ++j) {
						if (i == 0 && j == 0) {
							continue;
						}
						Bonus bonus = new Bonus(type, big, posX + size * i, posY + size * j);
						bonus.dirX = i;
						bonus.dirY = j;
						bonus.timer = 5000;
						world.addBonus(bonus);
					}
				}
				score = 55000;
				break;
			}
		}
		snake.gain(score);
		world.removeBonus(this);
	}

	public boolean contains(Point otherPoint) {
		if (this.isConsumed() || otherPoint == null) {
			return false;
		}
		int x = otherPoint.getX();
		int y = otherPoint.getY();
		int size = this.getSize();
		int x0 = this.posX;
		int x1 = x0 + size;
		int y0 = this.posY;
		int y1 = y0 + size;
		int columns = World.getColumns();
		int rows = World.getRows();
		if (x1 > x + columns) {
			x += columns;
		}
		if (y1 > y + rows) {
			y += rows;
		}
		return x >= x0 && x < x1 && y >= y0 && y < y1;
	}

	private int getSize() {
		return this.big ? 7 : 3;
	}

	public void fromString(String string) { // version réseau seulement
		if (string.length() != 0 && !string.endsWith("\n")) {
			string += "\n";
		}
		try {
			BufferedReader reader = new BufferedReader(new StringReader(string));
			String line1;
			if ((line1 = reader.readLine()) != null) {
				this.type = BonusType.values()[Integer.parseInt(line1)];
			}
			if ((line1 = reader.readLine()) != null) {
				this.big = Boolean.parseBoolean(line1);
			}
			if ((line1 = reader.readLine()) != null) {
				this.posX = Integer.parseInt(line1); // TODO: synchroniser
			}
			if ((line1 = reader.readLine()) != null) {
				this.posY = Integer.parseInt(line1); // TODO: synchroniser
			}
			if ((line1 = reader.readLine()) != null) {
				this.dirX = Integer.parseInt(line1); // TODO: synchroniser
			}
			if ((line1 = reader.readLine()) != null) {
				this.dirY = Integer.parseInt(line1); // TODO: synchroniser
			}
			if ((line1 = reader.readLine()) != null) {
				this.consumed = Boolean.parseBoolean(line1);
			}
			reader.close();
		} catch (Exception error) {}
	}

	public String toString() { // version réseau seulement
		String string = "";
		try {
			BufferedWriter writer = new BufferedWriter(new StringWriter());
			writer.write(Integer.toString(this.type.ordinal()) + "\n");
			writer.write(Boolean.toString(this.big) + "\n");
			writer.write(Integer.toString(this.posX) + "\n");
			writer.write(Integer.toString(this.posY) + "\n");
			writer.write(Integer.toString(this.dirX) + "\n");
			writer.write(Integer.toString(this.dirY) + "\n");
			writer.write(Boolean.toString(this.consumed) + "\n");
			string = writer.toString();
			writer.close();
		} catch (Exception error) {}
		return string;
	}

}
