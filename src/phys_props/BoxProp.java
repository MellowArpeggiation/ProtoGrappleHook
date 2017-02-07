package phys_props;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.Main;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.shapes.Box;
import game.Entity;

public class BoxProp implements Entity {
	
	int xSize;
	int ySize;
	Body mainBody;
	Box mainShape;
	
	public BoxProp(int newXSize, int newYSize, Vector2f newPosition, float newRotation, boolean isStatic) {
		create(newXSize, newYSize, newPosition, newRotation, isStatic);
	}
	
	public BoxProp(int newSize, Vector2f newPosition, float newRotation, boolean isStatic) {
		create(newSize, newSize, newPosition, newRotation, isStatic);
	}
	
	private void create(int newXSize, int newYSize, Vector2f newPosition, float newRotation, boolean isStatic) {
		xSize = newXSize;
		ySize = newYSize;
		mainShape = new Box(newXSize, newYSize);
		if (isStatic) {
			mainBody = new StaticBody(mainShape);
		} else {
			mainBody = new Body(mainShape, (newYSize + newXSize) / 2);
		}
		
		mainBody.setPosition(newPosition.x, newPosition.y);
		mainBody.setRotation(newRotation);
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
		graphics.fillRect(-xSize / 2, -ySize / 2, xSize, ySize);
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
