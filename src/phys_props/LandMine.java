package phys_props;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.Main;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.shapes.Box;
import game.Entity;

public class LandMine implements Entity {
	
	float explosiveForce;
	Body mainBody;
	Box mainShape;
	
	String name;
	FixedJoint mainJoint;
	
	boolean autoRemove;
	
	public LandMine(Vector2f position, float force, float rotation, Body attachedBody) {
		mainShape = new Box(20, 10);
		mainBody = new Body(mainShape, 10);
		mainBody.setPosition(position.x, position.y);
		mainBody.setRotation(rotation);
		mainBody.setUserData(this);
		
		Main.mainGame.gameWorld.add(mainBody);
		
		if (attachedBody != null) {
			mainJoint = new FixedJoint(mainBody, attachedBody);
			Main.mainGame.gameWorld.add(mainJoint);
		}
		
		explosiveForce = force;
	}

	@Override
	public Body getBody() {
		return mainBody;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void paint(Graphics2D graphics, AffineTransform currentTransform) {
		Vector2f position = (Vector2f) mainBody.getPosition();
		graphics.setColor(Color.red);
		graphics.translate(position.x, position.y);
		graphics.rotate(mainBody.getRotation());
		graphics.fillRect(-10, -5, 20, 10);
		graphics.setColor(Color.black);
	}

	@Override
	public void remove() {
		Main.mainGame.gameWorld.remove(mainBody);
		if (mainJoint != null) {
			Main.mainGame.gameWorld.remove(mainJoint);
		}
	}

	@Override
	public void setName(String newName) {
		name = newName;
	}

	@Override
	public void update() {

	}

	@Override
	public void onCollide(Entity entity2, Vector2f normal) {
		if (entity2 == null) return;
		Body entity2Body = entity2.getBody();
		if (entity2Body == null) return;
		if (entity2Body.isStatic()) return;
		if (entity2.getName() == "playerhook") return;
		Main.mainGame.createExplosion((Vector2f) mainBody.getPosition(), explosiveForce);
		autoRemove = true;
	}

	@Override
	public boolean doRemove() {
		return autoRemove;
	}

}
