package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.Timer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import main.Main;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;
import net.phys2d.raw.World;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import other_ents.Camera;
import other_ents.ExplosionSource;
import other_ents.VisibleString;
import phys_props.BoxProp;
import phys_props.CircleProp;
import phys_props.LandMine;
import phys_props.Player;

public class Game implements KeyListener, ActionListener, MouseListener,
		MouseMotionListener, CollisionListener {

	Entity[] entities;
	Timer timer;
	Image backBuffer;
	public World gameWorld;
	public static final AffineTransform IDENTITY_MATRIX = new AffineTransform();
	public boolean wKeyDown, aKeyDown, sKeyDown, dKeyDown;

	public boolean debugMode = false;

	public Point mousePosition;

	public Player playerCharacter;
	Camera activeCamera, playerCamera;

	String currentLevelName;
	private boolean isSlowed;
	float gameSpeed = 0.02f;

	boolean runEditor;

	public Game() {
		
	}

	/**
	 * false for gameplay, true for editing
	 * 
	 * @param mode, levelName
	 */
	public void start(boolean isEditing, String levelName) {
		timer = new Timer(17, this);
		backBuffer = Main.appletWindow.createImage(Main.SCREEN_WIDTH,
				Main.SCREEN_HEIGHT);
		gameWorld = new World(new Vector2f(0, 400), 1);
		gameWorld.enableRestingBodyDetection(10, 10, 10);
		gameWorld.addListener(this);
		
		runEditor = isEditing;

		entities = new Entity[1024];
		currentLevelName = levelName;
		Main.appletWindow.addKeyListener(this);
		Main.appletWindow.addMouseListener(this);
		Main.appletWindow.addMouseMotionListener(this);
		if (levelName != null) {
			Document levelXML = null;
			InputStream levelData = null;
			/*try {
				URL tmpURL = new URL(Main.codeBase, "levels/" + levelName);
				levelData = tmpURL.openStream();
				DocumentBuilder docReader = DocumentBuilderFactory
						.newInstance().newDocumentBuilder();
				levelXML = docReader.parse(levelData);
			} catch (IOException e) {
				e.printStackTrace();
				stop();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				stop();
			} catch (SAXException e) {
				e.printStackTrace();
				stop();
			} finally {
				Main.appletWindow.requestFocus();
			}*/
			
			File level = new File(System.getProperty("user.dir"), "levels/" + levelName);
			try {
				levelData = new FileInputStream(level);
				DocumentBuilder docReader = DocumentBuilderFactory
						.newInstance().newDocumentBuilder();
				levelXML = docReader.parse(levelData);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initLevel(levelXML);
			timer.start();
		}
	}

	public void stop() {
		Main.appletWindow.removeKeyListener(this);
		Main.appletWindow.removeMouseListener(this);
		Main.appletWindow.removeMouseMotionListener(this);
		Main.mainMenu.showMenu();
		timer.stop();
		destroyLevel();
	}

	private void initLevel(Document levelData) {
		Element levelRoot = levelData.getDocumentElement();
		NodeList allNodes = levelRoot.getChildNodes();
		for (int i = 0; i < allNodes.getLength(); i++) {
			Node currentItem = allNodes.item(i);
			if (currentItem.getNodeName() == "player") {
				createPlayer(currentItem);
			} else if (currentItem.getNodeName() == "entities") {
				createEntities(currentItem);
			}
		}

		if (debugMode) {
			addEntity(new VisibleString("DEBUG MODE!", new Vector2f(650, 70)));
		}
		addEntity(new VisibleString("Esc to return to menu", new Vector2f(650,
				30)));
		addEntity(new VisibleString("Level: " + currentLevelName, new Vector2f(650, 50)));
		addEntity(playerCamera);
		activeCamera = playerCamera;
		
		/*
		 * DEBUG CODE
		 */
/*		BoxProp newBoxes[] = new BoxProp[20];
		for (int i = 0; i < newBoxes.length; i++) {
			newBoxes[i] = new BoxProp((int) (Math.random() * 30 + 10), new Vector2f((float) Math.random() * 100, (float) Math.random() * 100), 0, false);
			addEntity(newBoxes[i]);
		}*/
	}

	private void createPlayer(Node currentItem) {
		NodeList tmpList1;
		NodeList tmpList2;
		float x = 0, y = 0;
		int health = 10;
		String name = null;
		if (currentItem.hasAttributes())
			name = currentItem.getAttributes().getNamedItem("name").getTextContent();
		tmpList1 = currentItem.getChildNodes();
	
		for (int o = 0; o < tmpList1.getLength(); o++) {
			if (tmpList1.item(o).getNodeName() == "health") {
				health = Integer.parseInt(tmpList1.item(o).getTextContent());
			} else if (tmpList1.item(o).getNodeName() == "spawnposition") {
				tmpList2 = tmpList1.item(o).getChildNodes();
	
				for (int p = 0; p < tmpList2.getLength(); p++) {
					if (tmpList2.item(p).getNodeName() == "x") {
						x = Float.parseFloat(tmpList2.item(p).getTextContent());
					} else if (tmpList2.item(p).getNodeName() == "y") {
						y = Float.parseFloat(tmpList2.item(p).getTextContent());
					}
				}
	
			}
		}
		
		playerCharacter = new Player(new Vector2f(x, y), health);
		playerCharacter.setName(name);
		addEntity(playerCharacter);
	}

	private void createEntities(Node currentItem) {
		NodeList tmpList1;
		tmpList1 = currentItem.getChildNodes();

		for (int o = 0; o < tmpList1.getLength(); o++) {
			Node currentEntityNode = tmpList1.item(o);
			if (currentEntityNode.getNodeName() == "boxprop") {
				createBoxProp(currentEntityNode);
			} else if (currentEntityNode.getNodeName() == "circleprop") {
				createCircleProp(currentEntityNode);
			} else if (currentEntityNode.getNodeName() == "camera") {
				createCamera(currentEntityNode);
			} else if (currentEntityNode.getNodeName() == "landmine") {
				createLandMine(currentEntityNode);
			} else if (currentEntityNode.getNodeName() == "visiblestring") {
				createVisibleString(currentEntityNode);
			}
		}
	}

	/**
	 * @param currentEntityNode
	 */
	private void createVisibleString(Node currentEntityNode) {
		NodeList tmpList2;
		float x = 0, y = 0;
		String text = "";
		String name = null;
		
		if (currentEntityNode.hasAttributes())
			name = currentEntityNode.getAttributes().getNamedItem("name").getTextContent();
		tmpList2 = currentEntityNode.getChildNodes();
	
		for (int p = 0; p < tmpList2.getLength(); p++) {
			if (tmpList2.item(p).getNodeName() == "x") {
				x = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "y") {
				y = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "text") {
				text = tmpList2.item(p).getTextContent();
			} 
		}
	
		VisibleString newString = new VisibleString(text, new Vector2f(x, y));
		newString.setName(name);
		addEntity(newString);
	}

	/**
	 * @param currentEntityNode
	 */
	private void createCircleProp(Node currentEntityNode) {
		NodeList tmpList2;
		float x = 0, y = 0;
		int size = 0;
		boolean isStatic = false;
		String name = null;
		
		if (currentEntityNode.hasAttributes())
			name = currentEntityNode.getAttributes().getNamedItem("name").getTextContent();
		tmpList2 = currentEntityNode.getChildNodes();

		for (int p = 0; p < tmpList2.getLength(); p++) {
			if (tmpList2.item(p).getNodeName() == "x") {
				x = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "y") {
				y = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "size") {
				size = Integer.parseInt(tmpList2.item(p)
						.getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "static") {
				isStatic = Boolean.parseBoolean((tmpList2.item(p)
						.getTextContent()));
			}
		}

		CircleProp newCircle = new CircleProp(size, new Vector2f(x, y),
				isStatic);
		newCircle.setName(name);
		addEntity(newCircle);
	}

	/**
	 * @param currentEntityNode
	 */
	private void createBoxProp(Node currentEntityNode) {
		NodeList tmpList2;
		float x = 0, y = 0, rotation = 0;
		int sizex = 0, sizey = 0;
		boolean isStatic = false;
		String name = null;
		
		if (currentEntityNode.hasAttributes())
			name = currentEntityNode.getAttributes().getNamedItem("name").getTextContent();
		tmpList2 = currentEntityNode.getChildNodes();

		for (int p = 0; p < tmpList2.getLength(); p++) {
			if (tmpList2.item(p).getNodeName() == "x") {
				x = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "y") {
				y = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "sizex") {
				sizex = Integer.parseInt(tmpList2.item(p)
						.getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "sizey") {
				sizey = Integer.parseInt(tmpList2.item(p)
						.getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "rotation") {
				rotation = Float.parseFloat(tmpList2.item(p)
						.getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "static") {
				isStatic = Boolean.parseBoolean((tmpList2.item(p)
						.getTextContent()));
			}
		}

		BoxProp newBox = new BoxProp(sizex, sizey, new Vector2f(x, y),
				rotation, isStatic);
		newBox.setName(name);
		addEntity(newBox);
	}
	
	/**
	 * @param currentEntityNode
	 */
	private void createLandMine(Node currentEntityNode) {
		NodeList tmpList2;
		float x = 0, y = 0, rotation = 0, explosiveForce = 10;
		String name = null;
		Body attachedBody = null;
		
		if (currentEntityNode.hasAttributes())
			name = currentEntityNode.getAttributes().getNamedItem("name").getTextContent();
		tmpList2 = currentEntityNode.getChildNodes();

		for (int p = 0; p < tmpList2.getLength(); p++) {
			if (tmpList2.item(p).getNodeName() == "x") {
				x = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "y") {
				y = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "rotation") {
				rotation = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "explosiveforce") {
				explosiveForce = Float.parseFloat(tmpList2.item(p).getTextContent());
			} else if (tmpList2.item(p).getNodeName() == "attachedbody") {
				Entity matchedEntity = getEntity(tmpList2.item(p));
				if (matchedEntity != null) {
					if (matchedEntity.getBody() != null) {
						attachedBody = matchedEntity.getBody();
					}
				}
			}
		}

		LandMine newMine = new LandMine(new Vector2f(x, y), explosiveForce, rotation, attachedBody);
		newMine.setName(name);
		addEntity(newMine);
	}

	/**
	 * @param currentEntityNode
	 */
	private void createCamera(Node currentEntityNode) {
		NodeList tmpList2;
		Entity followObject = null;
		boolean isActive = false;
		String name = null;
		
		if (currentEntityNode.hasAttributes())
			name = currentEntityNode.getAttributes().getNamedItem("name").getTextContent();
		tmpList2 = currentEntityNode.getChildNodes();
	
		for (int p = 0; p < tmpList2.getLength(); p++) {
			if (tmpList2.item(p).getNodeName() == "followobject") {
				followObject = getEntity(tmpList2.item(p));
			} else if (tmpList2.item(p).getNodeName() == "isactive") {
				isActive = Boolean.parseBoolean((tmpList2.item(p)
						.getTextContent()));
			}
		}
	
		Camera newCamera = new Camera(followObject.getBody());
		newCamera.setName(name);
		if (isActive)
			playerCamera = newCamera;
	}

	/**
	 * @param EntityMatch
	 * @param currentNode
	 * @return
	 */
	private Entity getEntity(Node currentNode) {
		Entity entityMatch = null;
		for (int ent = 0; ent < entities.length; ent++) {
			if (entities[ent] == null)
				continue;
			if (entities[ent].getName() == null)
				continue;
			if (currentNode.getTextContent().matches(
					entities[ent].getName())) {
				entityMatch = entities[ent];
			}
		}
		return entityMatch;
	}

	private void destroyLevel() {
		clearEntities();
		gameWorld.clear();
		playerCharacter = null;
		playerCamera = null;
	}

	private void paintScene() {
		Graphics2D graphics = (Graphics2D) Main.appletWindow.getGraphics();
		Graphics2D bufferGraphics = (Graphics2D) backBuffer.getGraphics();
		Main.appletWindow.paint(bufferGraphics);
		AffineTransform currentCameraTransform;
		if (activeCamera != null) {
			currentCameraTransform = activeCamera.getTransform();
		} else {
			currentCameraTransform = IDENTITY_MATRIX;
		}
		for (int i = 0; i < entities.length; i++) {
			if (entities[i] != null) {
				bufferGraphics.setTransform(currentCameraTransform);
				entities[i].paint(bufferGraphics, currentCameraTransform);
			}
		}
		graphics.drawImage(backBuffer, 0, 0, null);
	}

	private void updateScene() {
		for (int i = 0; i < entities.length; i++) {
			if (entities[i] != null) {
				entities[i].update();
				if (entities[i].doRemove()) {
					delEntity(entities[i]);
				}
			}
		}
		gameWorld.step(gameSpeed);
	}

	private void updateEditor() {
		activeCamera.update();
	}

	public void addEntity(Entity newEntity) {
		for (int i = 0; i < entities.length; i++) {
			if (entities[i] == null) {
				entities[i] = newEntity;
				return;
			}
		}
		System.err.println("Entity limit reached, entity not added!");
		System.err.println("Ask a programmer to increase mainGame.maxEntities");
	}

	public void delEntity(Entity entityName) {
		for (int i = 0; i < entities.length; i++) {
			if (entities[i] == entityName) {
				entities[i].remove();
				entities[i] = null;
				return;
			}
		}
		System.err.println("Entity " + entityName.toString()
				+ " Does not exist");
	}

	public void clearEntities() {
		for (int i = 0; i < entities.length; i++) {
			if (entities[i] != null) {
				entities[i].remove();
				entities[i] = null;
			}
		}
	}

	private void toggleSlowed() {
		if (!isSlowed) {
			gameWorld.setGravity(0, 200);
			isSlowed = true;
			gameSpeed = 0.004f;
		} else {
			gameWorld.setGravity(0, 400);
			isSlowed = false;
			gameSpeed = 0.02f;
		}
	}

	private void swapCamera() {
		if (activeCamera == playerCamera) {
			activeCamera = null;
		} else {
			activeCamera = playerCamera;
		}
	}

	@Override
	public void collisionOccured(CollisionEvent event) {
		Body body1, body2;
		body1 = event.getBodyA();
		body2 = event.getBodyB();
	
		Vector2f normal = (Vector2f) event.getNormal();
		
		if (body1.getUserData() != null) {
			((Entity) body1.getUserData()).onCollide((Entity) body2.getUserData(), normal);
		}
		if (body2.getUserData() != null) {
			normal = normal.negate();
			((Entity) body2.getUserData()).onCollide((Entity) body1.getUserData(), normal);
		}
	}

	public void createExplosion(Vector2f position, float explosiveForce) {
			ExplosionSource explosion = new ExplosionSource(position, explosiveForce);
			for (int i = 0; i < entities.length; i++) {
				if (entities[i] != null) {
					Body body = entities[i].getBody();
					if (body == null) continue;
	//				if (entities[i].equals(playerCharacter)) continue;
					explosion.apply(body, gameSpeed);
				}
			}
			explosion = null;
		}

	public static float findAngleFromVector(Vector2f vector1) {
		float angle;
		Vector2f vector2 = new Vector2f();
		vector2 = new Vector2f(1, 0);
		vector2.normalise();
		vector1.normalise();
	
		angle = vector1.dot(vector2);
		angle = (float) Math.acos(angle);
		if (vector1.y > 0) {
			return angle;
		} else {
			return -angle;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		// System.out.println(key);
		if (key == 27) {
			stop();
		}
		if (runEditor) {
			
		} else {
			if (key == 87) {
				wKeyDown = true;
			} else if (key == 65) {
				aKeyDown = true;
			} else if (key == 83) {
				sKeyDown = true;
			} else if (key == 68) {
				dKeyDown = true;
			} else if (key == 67) {
				swapCamera();
			} else if (key == 82) {
				stop();
				Main.mainMenu.hideMenu();
				start(false, currentLevelName);
			} else if (key == 70) {
	//			toggleSlowed();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == 87) {
			wKeyDown = false;
		} else if (key == 65) {
			aKeyDown = false;
		} else if (key == 83) {
			sKeyDown = false;
		} else if (key == 68) {
			dKeyDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (runEditor) {
			updateEditor();
		} else {
			updateScene();
		}
		paintScene();
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Main.appletWindow.requestFocus();
		int button = e.getButton();
		if (button == 1) {
			mousePosition = e.getPoint();
			if (activeCamera != null) {
				mousePosition.translate((int) -activeCamera.getTransform()
						.getTranslateX(), (int) -activeCamera.getTransform()
						.getTranslateY());
			}
			playerCharacter.shootRope(new Vector2f(mousePosition.x,
					mousePosition.y));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if (button == 1) {
			playerCharacter.destroyRope();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mousePosition = arg0.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}
}
