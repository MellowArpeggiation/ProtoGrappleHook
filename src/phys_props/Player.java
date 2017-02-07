package phys_props;

import game.Entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.Main;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.shapes.Box;

public class Player implements Entity {

	Box mainShape;
	Body mainBody;
	Rope rope;
	Body grapplePoint;
	FixedJoint grappleJoint;
	Hook playerHook;
	Body touchingBody = null;
	Body hookedBody = null;
	private Vector2f contactNormal = new Vector2f(0, -4);
	Vector2f startPosition;
	boolean oldWValue = false;

	String name;
	int health;

	float jumpForce = 480000;
	float sideForce = 0.6f;
	int mass = 40;
	float addY = -1.2f;
	float moveForce = 28000;

	public boolean isAlive = true;

	public Player(Vector2f newPosition, int newHealth) {
		startPosition = newPosition;
		mainShape = new Box(20, 40);
		mainBody = new Body(mainShape, mass);
		mainBody.setPosition(newPosition.x, newPosition.y);
		mainBody.setRotatable(false);
		mainBody.setCanRest(false);
		mainBody.setFriction(1.5f);
		mainBody.setUserData(this);
		Main.mainGame.gameWorld.add(mainBody);

		health = newHealth;
	}

	@Override
	public void paint(Graphics2D graphics, AffineTransform currentTransform) {
		Vector2f position = (Vector2f) mainBody.getPosition();
		graphics.translate(position.x, position.y);
		graphics.setColor(Color.BLUE);
		graphics.fillRect(-10, -20, 20, 40);
		graphics.setColor(Color.BLACK);
	}

	@Override
	public void remove() {
		Main.mainGame.gameWorld.remove(mainBody);
		if (rope != null) {
			Main.mainGame.delEntity(rope);
			rope = null;
		}
	}

	@Override
	public void update() {
		if (Main.mainGame.aKeyDown) {
			if (touchingBody != null) {
				mainBody.addForce(new Vector2f(-moveForce, 0));
			} else {
				mainBody.addForce(new Vector2f(-moveForce / 2, 0));
			}
		}
		if (Main.mainGame.dKeyDown) {
			if (touchingBody != null) {
				mainBody.addForce(new Vector2f(moveForce, 0));
			} else {
				mainBody.addForce(new Vector2f(moveForce / 2, 0));
			}
		}

		if (Main.mainGame.wKeyDown) {
			if (touchingBody != null) {
				Vector2f newForce = new Vector2f(contactNormal);
				newForce.x = newForce.x * sideForce;
				if (newForce.y > -0.5f) {
					newForce.add(new Vector2f(0, addY));
					mainBody.adjustVelocity(new Vector2f(-mainBody.getVelocity()
							.getX() + touchingBody.getVelocity().getX(), -mainBody.getVelocity().getY() + touchingBody.getVelocity().getY()));
				}
				newForce.scale(jumpForce);
				mainBody.addForce(newForce);
				touchingBody = null;
			}
		}
		
		CollisionEvent[] contacts = Main.mainGame.gameWorld.getContacts(mainBody);
		if (contacts.length == 0) touchingBody = null;
	}

	public void createRope(Vector2f position1, Vector2f position2, Body otherObj) {
		Main.mainGame.delEntity(playerHook);
		playerHook = null;
		hookedBody = otherObj;
		float length = position1.distance(position2);
		grapplePoint = new Body(new Box(3, 3), 3);
		grapplePoint.setFriction(0);
		grapplePoint.setPosition(position2.x, position2.y);
		grapplePoint.addExcludedBody(otherObj);
		grapplePoint.addExcludedBody(mainBody);
		grappleJoint = new FixedJoint(grapplePoint, otherObj);
		Main.mainGame.gameWorld.add(grapplePoint);
		Main.mainGame.gameWorld.add(grappleJoint);
		if (length > 200) {
			rope = new Rope(mainBody, grapplePoint, 40);
		} else {
			rope = new Rope(mainBody, grapplePoint, 30);
		}
		Main.mainGame.addEntity(rope);
	}

	public void destroyRope() {
		if (rope != null) {
			Main.mainGame.delEntity(rope);
			Main.mainGame.gameWorld.remove(grapplePoint);
			Main.mainGame.gameWorld.remove(grappleJoint);
			rope = null;
			grapplePoint = null;
		}
		if (playerHook != null) {
			Main.mainGame.delEntity(playerHook);
			playerHook = null;
		}
		hookedBody = null;
	}

	public void shootRope(Vector2f mousePosition) {
		Vector2f position1 = (Vector2f) mainBody.getPosition();
		playerHook = new Hook(position1, mousePosition, this);
		playerHook.setName("playerhook");
		Main.mainGame.addEntity(playerHook);
	}

	@Override
	public Body getBody() {
		return mainBody;
	}

	public void setContactNormal(Vector2f contactNormal) {
		this.contactNormal = contactNormal;
	}

	public Vector2f getContactNormal() {
		return contactNormal;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String newName) {
		name = newName;
	}

	@Override
	public void onCollide(Entity object2, Vector2f normal) {
		if (object2 == null) return;
		Body object2Body = object2.getBody();
		if (!object2Body.isStatic()) normal = normal.negate();
		if (object2Body == grapplePoint || (object2Body == hookedBody && !object2Body.isStatic())) return;
		if (normal.y < 0.5f) {
			setContactNormal(normal);
			touchingBody = object2Body;
		}
	}

	@Override
	public boolean doRemove() {
		return false;
	}

	private class Hook implements Entity {

		Vector2f position1, position2;
		Body hookBody;
		
		String name;

		public Hook(Vector2f newPosition1, Vector2f newPosition2,
				Entity playerParent) {
			position1 = newPosition1;
			position2 = newPosition2;

			hookBody = new Body(new Box(10, 10), 2);
			hookBody.addExcludedBody(playerParent.getBody());
			hookBody.setPosition(position1.x, position1.y);
			hookBody.setUserData(this);
			hookBody.adjustVelocity((Vector2f)
					playerParent.getBody().getVelocity());

			Main.mainGame.gameWorld.add(hookBody);

			fire();
		}

		private void fire() {
			Vector2f pointDiff = new Vector2f(position2);
			pointDiff.sub(position1);
			pointDiff.normalise();
			pointDiff.scale(900);
			hookBody.adjustVelocity(pointDiff);
		}

		@Override
		public Body getBody() {
			return hookBody;
		}

		@Override
		public void paint(Graphics2D graphics, AffineTransform currentTransform) {
			Vector2f playerPosition = (Vector2f) mainBody.getPosition();
			Vector2f hookPosition = (Vector2f) hookBody.getPosition();
			
			graphics.setStroke(new BasicStroke(2));
			
			graphics.drawLine((int) playerPosition.x, (int) playerPosition.y,
					(int) hookPosition.x, (int) hookPosition.y);
			graphics.translate(hookPosition.x, hookPosition.y);
			if (Main.mainGame.debugMode) {
				graphics.setColor(Color.GREEN);
				graphics.drawOval(-5, -5, 10, 10);
				graphics.setColor(Color.BLACK);
			}
			
			graphics.setStroke(new BasicStroke(1));
		}

		@Override
		public void remove() {
			Main.mainGame.gameWorld.remove(hookBody);
			hookBody = null;
		}

		@Override
		public void update() {

		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String newName) {
			name = newName;
		}

		@Override
		public void onCollide(Entity object2, Vector2f normal) {
			createRope(position1, (Vector2f) hookBody.getPosition(), object2.getBody());
		}

		@Override
		public boolean doRemove() {
			return false;
		}
	}
}
