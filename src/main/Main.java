/**
 * Copyright George Paton 2011
 */
package main;

import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;

import game.Game;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author George Paton
 * @version 0.4.3
 */
public class Main extends JApplet {
	private static final long serialVersionUID = -2939516166476686993L;
	public static final byte MAJOR_VERSION = 0;
	public static final byte MINOR_VERSION = 4;
	public static final byte REVISION_NUMBER = 3;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static Main appletWindow;
	public static URL codeBase;
	public static Game mainGame;
	public static Menu mainMenu;
	static boolean gameStarted;
	
	public static Image errorImage;
	
	public static void main(String[] args) {
		
		Main appletClass = new Main ();

		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(1, 1));
		frame.add(appletClass );

		// Set frame size and other properties
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

		// Call applet methods
		appletClass .init();
		appletClass .start();

		frame.setVisible(true);
	}
	
	public void init() {
	    try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	    	
	    }
	    catch (ClassNotFoundException e) {
	    	
	    }
	    catch (InstantiationException e) {
	    	
	    }
	    catch (IllegalAccessException e) {
	    	
	    }
		System.out.println("Grappling Hook Game Client Version: " + MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_NUMBER);
		appletWindow = this;
		//codeBase = appletWindow.getCodeBase();
		mainGame = new Game();
		mainMenu = new Menu();
		mainMenu.showMenu();
	}
}
