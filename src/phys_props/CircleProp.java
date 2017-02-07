package phys_props;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.Main;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Circle;

import game.Entity;

public class CircleProp implements Entity {
	
	int size;
	Body mainBody;
	Circle mainShape;
	
	public CircleProp(int newSize, Vector2f newPosition, boolean isStatic) {
		size = newSize;
		mainShape = new Circle(size);
		if (isStatic) {
			mainBody = new StaticBody(mainShape);
		} else {
			mainBody = new Body(mainShape, size);
		}
		size = size * 2;
		mainBody.setPosition(newPosition.x, newPosition.y);
		mainBody.setUserData(this);
		Main.mainGame.gameWorld.add(mainBody);
	}

	@Override
	public void update() {

	}

	@Override
	public void paint(Graphics2D graphics, AffineTransform currentTransform) {
		Vector2f position = (Vector2f) mainBody.getPosition();
		graphics.translate(position.x, position.y);
		graphics.rotate(mainBody.getRotation());
		graphics.fillOval(-size / 2, -size / 2, size, size);
		graphics.setColor(Color.WHITE);
		graphics.drawLine(-size / 2, 0, 0, 0);
		graphics.setColor(Color.BLACK);
	}

	@Override
	public void remove() {
		Main.mainGame.gameWorld.remove(mainBody);
	}

	@Override
	public Body getBody() {
		return mainBody;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String newName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCollide(Entity object2, Vector2f normal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean doRemove() {
		// TODO Auto-generated method stub
		return false;
	}
}
