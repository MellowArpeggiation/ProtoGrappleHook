package other_ents;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.forcesource.ForceSource;


public class ExplosionSource implements ForceSource{
	
	Vector2f position;
	float force;
	
	public ExplosionSource(Vector2f explosionPosition, float explosionForce) {
		position = explosionPosition;
		force = explosionForce;
	}
	
	public ExplosionSource(float x, float y, float explosionForce) {
		position = new Vector2f(x, y);
		force = explosionForce;
	}

	@Override
	public void apply(Body body, float dt) {
		if (body.isStatic()) return;
		Vector2f forceCreated = new Vector2f(body.getPosition());
		float distanceSq = forceCreated.distance(position) + 1;
		forceCreated.sub(position);
		forceCreated.normalise();
		forceCreated.scale(force * (5000 / distanceSq) * (0.0005f / dt));
		
		body.addForce(forceCreated);
	}
}
