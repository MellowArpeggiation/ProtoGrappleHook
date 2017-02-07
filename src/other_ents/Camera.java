package other_ents;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.Main;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import game.Entity;

public class Camera implements Entity {
	
	Body followObject;
	AffineTransform cameraTransform;
	
	public Camera(Body newFollowObject) {
		followObject = newFollowObject;
	}

	@Override
	public Body getBody() {
		return null;
	}

	@Override
	public void remove() {
		
	}

	@Override
	public void update() {
		Vector2f position = (Vector2f) followObject.getPosition();
		cameraTransform = new AffineTransform();
		cameraTransform.translate(-position.x + (Main.SCREEN_WIDTH / 2), -position.y + (Main.SCREEN_HEIGHT / 2));
	}

	public AffineTransform getTransform() {
		return cameraTransform;
	}

	@Override
	public void paint(Graphics2D graphics, AffineTransform currentTransform) {
		
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String newName) {
		
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
