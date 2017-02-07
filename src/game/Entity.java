package game;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;

public interface Entity {
	public void update();
	public void paint(Graphics2D graphics, AffineTransform currentTransform);
	public void remove();
	public Body getBody();
	
	public String getName();
	public void setName(String newName);
	
	public void onCollide(Entity entity2, Vector2f normal);
	
	public boolean doRemove();
}
