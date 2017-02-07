package other_ents;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

import game.Entity;
import game.Game;

public class VisibleString implements Entity {
	
	String[] text;
	Vector2f position;
	
	public VisibleString(String newText, Vector2f newPosition) {
		if (newText == null) newText = "null";
		text = newText.split("\n");
		position = newPosition;
	}

	@Override
	public void paint(Graphics2D graphics, AffineTransform currentTransform) {
		graphics.setTransform(Game.IDENTITY_MATRIX);
		for (int i = 0; i < text.length; i++) {
			graphics.drawString(text[i], position.x, position.y + i * 20);
		}
	}

	@Override
	public void remove() {
		
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
