package games.snake3000;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

public class Snake {

	private Color color;
	private String name;
	private int score;
	private int direction;
	private int speed;
	private int movePeriod;
	private int moveCountdown;
	private int invincibilityCountdown;
	private int leftKey;
	private int rightKey;
	private boolean reversed;
	private List<Point> body;

	public Snake(Color color, String name, int leftKey, int rightKey, int posX) {
		this.color = color;
		this.name = name;
		this.score = 0;
		this.direction = 0;
		this.speed = 10;
		this.movePeriod = 250;
		this.moveCountdown = 0;
		this.invincibilityCountdown = 0;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
		this.reversed = false;
		List<Point> body = new ArrayList<Point>();
		int rows = World.getRows();
		for (int i = 0; i < 10; i++) {
			int posY = ((rows + i - 10) % rows + rows) % rows;
			body.add(new Point(posX, posY));
		}
		this.body = body;
	}

	public void update(GameContainer container, StateBasedGame game, int delta) {
		if (this.isDead()) {
			return;
		}
		Input input = container.getInput();
		boolean leftKeyDown = input.isKeyPressed(this.leftKey);
		boolean rightKeyDown = input.isKeyPressed(this.rightKey);
		if (leftKeyDown != rightKeyDown) {
			int direction = this.direction + 1;
			if (leftKeyDown) {
				direction += 2;
			}
			if (this.reversed) {
				direction += 2;
			}
			this.direction = direction % 4;
		}
		int invincibilityCountdown = this.invincibilityCountdown;
		if (invincibilityCountdown > 0) {
			this.invincibilityCountdown = Math.max(invincibilityCountdown - delta, 0);
		}
		int movePeriod = this.movePeriod;
		int moveCountdown = this.moveCountdown;
		moveCountdown += delta * speed;
		while (moveCountdown > 0) {
			this.expand();
			this.shrink();
			moveCountdown -= movePeriod;
		}
		this.moveCountdown = moveCountdown;
		this.score += delta;
	}

	public void render(GameContainer container, StateBasedGame game, Graphics context) {
		if (this.isDead()) {
			return;
		}
		if (this.invincibilityCountdown % 500 >= 250) {
			int r = this.color.getRed();
			int g = this.color.getGreen();
			int b = this.color.getBlue();
			int a = 128;
			context.setColor(new Color(r, g, b, a));
		} else {
			context.setColor(this.color);
		}
		int caseSize = World.getCaseSize();
		for (Point point: this.body) {
			context.fillRect(point.getX() * caseSize, point.getY() * caseSize, caseSize, caseSize);
		}
	}

	public boolean isDead() {
		return this.body.size() == 0;
	}

	public boolean isInvincible() {
		return !this.isDead() && this.invincibilityCountdown > 0;
	}

	public void kill() {
		if (this.isDead() || this.isInvincible()) {
			return;
		}
		this.body.clear();
	}

	public void boost(int invincibilityCountdown) {
		if (this.isDead()) {
			return;
		}
		this.invincibilityCountdown = Math.max(invincibilityCountdown, 0);
	}

	public void shrink() {
		if (this.isDead()) {
			return;
		}
		List<Point> body = this.body;
		int size = body.size();
		if (size == 1) {
			return;
		}
		body.remove(size - 1);
	}

	public void expand() {
		if (this.isDead()) {
			return;
		}
		int direction = this.direction;
		int dirX = direction == 3 ? -1 : direction == 1 ? 1 : 0;
		int dirY = direction == 0 ? -1 : direction == 2 ? 1 : 0;
		int columns = World.getColumns();
		int rows = World.getRows();
		List<Point> body = this.body;
		Point oldHead = body.get(0);
		int x = ((oldHead.getX() + dirX) % columns + columns) % columns;
		int y = ((oldHead.getY() + dirY) % rows + rows) % rows;
		Point newHead = new Point(x, y);
		body.add(0, newHead);
	}

	public void decelerate() {
		if (this.isDead() || this.speed <= 4) {
			return;
		}
		this.speed -= 4;
	}

	public void accelerate() {
		if (this.isDead()) {
			return;
		}
		this.speed += 7;
	}

	public void reverse() {
		if (this.isDead()) {
			return;
		}
		this.reversed = !this.reversed;
	}

	public void gain(int score) {
		if (this.isDead()) {
			return;
		}
		this.score += score;
	}

	public boolean contains(Point otherPoint) {
		if (this.isDead() || otherPoint == null) {
			return false;
		}
		int x = otherPoint.getX();
		int y = otherPoint.getY();
		for (Point point: this.body) {
			if (point.getX() == x && point.getY() == y) {
				return true;
			}
		}
		return false;
	}

	public Point getHead() {
		if (this.isDead()) {
			return null;
		}
		return this.body.get(0);
	}

	public Color getColor() {
		return this.color;
	}

	public String getName() {
		return this.name;
	}

	public int getScore() {
		return this.score;
	}

}
