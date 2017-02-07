package phys_props;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import main.Main;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.BasicJoint;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Box;

import game.Entity;
import game.Game;

public class Rope implements Entity {
	
	Box mainShape;
	Body[] mainBodies;
	BasicJoint[] mainJoints;
	int length, lengthOverRes, res;
	final float segMass = 3f;
	float jointRelaxation = 0.9f;
	Vector2f point1, point2;

	@SuppressWarnings("unused")
	private Rope(Body newObj1, Body newObj2, int ropeResolution, int extraLength) {
		point1 = (Vector2f) newObj1.getPosition();
		point2 = (Vector2f) newObj2.getPosition();
		res = ropeResolution;
		createRope(extraLength, newObj1, newObj2);
	}
	
	public Rope(Body newObj1, Body newObj2, int ropeResolution) {
		point1 = (Vector2f) newObj1.getPosition();
		point2 = (Vector2f) newObj2.getPosition();
		res = ropeResolution;
		createRope(0, newObj1, newObj2);
	}
	
	private void createRope(int extraLength, Body object1, Body object2) {
		length = (int) point1.distance(point2);
		if (length < res) {
			length = res;
		}
		lengthOverRes = length / res;
		mainShape = new Box(res, 2);
		mainBodies = new Body[lengthOverRes];
		mainJoints = new BasicJoint[lengthOverRes + 1];
		createSegments(object1, object2);
	}

	/**
	 * @param object1
	 * @param object2
	 */
	private void createSegments(Body object1, Body object2) {
		Vector2f pointDiff = new Vector2f(point2);
		pointDiff.sub(point1);
		pointDiff.scale(1 / (float) (length));
		
		float rotation = Game.findAngleFromVector(pointDiff);
		
		mainBodies[0] = new Body(mainShape, segMass);
		mainBodies[0].addExcludedBody(object1);
		mainBodies[0].setPosition(point1.x, point1.y);
		mainBodies[0].setRotation(rotation);
		mainBodies[0].adjustVelocity((Vector2f) object1.getVelocity());
		mainBodies[0].setUserData(this);
		mainJoints[0] = new BasicJoint(object1, mainBodies[0], point1);
		mainJoints[0].setRelaxation(jointRelaxation);
		
		Main.mainGame.gameWorld.add(mainBodies[0]);
		Main.mainGame.gameWorld.add(mainJoints[0]);
		
		for (int i = 1; i < lengthOverRes; i++) {
			mainBodies[i] = new Body(mainShape, segMass);
			mainBodies[i].addExcludedBody(mainBodies[i-1]);
			mainBodies[i].addExcludedBody(object1);
			mainBodies[i].addExcludedBody(object2);
			Vector2f tmpPoint = new Vector2f(pointDiff);
			Vector2f tmpPoint2 = new Vector2f(pointDiff);
			tmpPoint.scale((float) (i) * res);
			tmpPoint2.scale((float) (i) * res - (res / 2));
			tmpPoint.add(point1);
			tmpPoint2.add(point1);
			mainBodies[i].setPosition(tmpPoint.x, tmpPoint.y);
			mainBodies[i].setRotation(rotation);
			mainBodies[i].adjustVelocity((Vector2f) object1.getVelocity());
			mainBodies[i].setUserData(this);
			mainJoints[i] = new BasicJoint(mainBodies[i-1], mainBodies[i], tmpPoint2);
			mainJoints[i].setRelaxation(jointRelaxation);
					
			Main.mainGame.gameWorld.add(mainBodies[i]);
			Main.mainGame.gameWorld.add(mainJoints[i]);
		}
		
		mainJoints[lengthOverRes] = new BasicJoint(mainBodies[lengthOverRes - 1], object2, point2);
		mainJoints[lengthOverRes].setRelaxation(jointRelaxation);
		Main.mainGame.gameWorld.add(mainJoints[lengthOverRes]);
	}

	@Override
	public void paint(Graphics2D graphics, AffineTransform currentTransform) {
		Vector2f position1 = (Vector2f) mainBodies[0].getPosition();
		Vector2f position2 = new Vector2f();
		
		graphics.setStroke(new BasicStroke(2));
		
		if (Main.mainGame.debugMode) {
			graphics.setColor(Color.green);
			graphics.translate(position1.x, position1.y);
			graphics.rotate(mainBodies[0].getRotation());
			graphics.drawRect(-res / 2, 0, res, 1);
			graphics.setTransform(currentTransform);
			graphics.setColor(Color.black);
		}
		for (int i = 1; i < lengthOverRes; i++) {
			position1 = (Vector2f) mainBodies[i].getPosition();
			position2 = (Vector2f) mainBodies[i-1].getPosition();
			graphics.drawLine((int) position1.x, (int) position1.y, (int) position2.x, (int) position2.y);
			
			if (Main.mainGame.debugMode) {
				graphics.setColor(Color.green);
				graphics.translate(position1.x, position1.y);
				graphics.rotate(mainBodies[i].getRotation());
				graphics.drawRect(-res / 2, 0, res, 1);
				graphics.setTransform(currentTransform);
				graphics.setColor(Color.black);
			}
		}
		graphics.drawLine((int) position1.x, (int) position1.y, (int) point2.x, (int) point2.y);
		
		graphics.setStroke(new BasicStroke(1));
	}

	@Override
	public void remove() {
		for (int i = 0; i < lengthOverRes; i++) {
			Main.mainGame.gameWorld.remove(mainBodies[i]);
			Main.mainGame.gameWorld.remove(mainJoints[i]);
			mainBodies[i] = null;
			mainJoints[i] = null;
		}
		Main.mainGame.gameWorld.remove(mainJoints[lengthOverRes]);
	}

	@Override
	public void update() {
		
	}

	@Override
	public Body getBody() {
		return null;
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
		
	}

	@Override
	public boolean doRemove() {
		// TODO Auto-generated method stub
		return false;
	}

}
