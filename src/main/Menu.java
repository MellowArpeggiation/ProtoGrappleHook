package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public class Menu implements ActionListener, ListSelectionListener {
	
	JPanel menu;
	String levelSelected = "test.xml", tmpLevelSelected;
	HelpMenu helpMenu;
	JDialog levelDialog;
	boolean runEditor;
	
	static Font menuFont = new Font(Font.DIALOG, Font.PLAIN, 24);
	
	Image grappleLogo;
	
	public Menu() {
		menu = new JPanel();
		menu.setLayout(null);
		helpMenu = new HelpMenu();
		
		InputStream url = getClass().getResourceAsStream("/logo/grapplelogo.png");
		try {
			grappleLogo = ImageIO.read(url);
		} catch (IOException e) {
			grappleLogo = Main.errorImage;
			e.printStackTrace();
		}
	}
	
	public void showMenu() {
		JButton play = new JButton("Play");
		play.setBounds(20, 20, 200, 80);
		play.addActionListener(this);
		play.setFont(menuFont);
		
		JButton help = new JButton("Help");
		help.setBounds(20, 120, 200, 80);
		help.addActionListener(this);
		help.setFont(menuFont);
		
		JButton levelEditor = new JButton("Editor");
		levelEditor.setBounds(20, 220, 200, 80);
		levelEditor.addActionListener(this);
		levelEditor.setFont(menuFont);
		
		JLabel logo = new JLabel(new ImageIcon(grappleLogo));
		logo.setBounds(400, 20, 300, 400);
		
		menu.add(play);
		menu.add(help);
		menu.add(levelEditor);
		menu.add(logo);
		Main.appletWindow.add(menu);
		menu.setVisible(true);

	}

	private void showLevelSelector(boolean gameType) {
		runEditor = gameType;
		tmpLevelSelected = null;
		
		levelDialog = new JDialog(levelDialog, "Select a level");
		levelDialog.setBounds(300, 300, 400, 400);
		levelDialog.setVisible(true);
		levelDialog.setLayout(null);
		JList<String> levelList = new JList<String>();
		JButton okayButton = new JButton("Select");
		JTextArea newLevelName = new JTextArea();
		JButton newLevelButton = new JButton("Create");
		
		File levelDir = new File(System.getProperty("user.dir"), "levels");
		System.out.println(levelDir);
		String[] list = levelDir.list();
		
		/*URL tmpURL = null;
		try {
			tmpURL = new URL(Main.codeBase, "levels");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println("Reading: " + tmpURL);
		
        // Get an input stream for reading
        InputStream in = null;
        
		try {
			in = tmpURL.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringWriter stringWriter = new StringWriter();
		
		// Repeat until break
		for (;;) {
			int data = 0;
			try {
				data = in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Check for EOF
			if (data == -1) {
				break;
			} else {
				stringWriter.append((char) data);
			}
		}
		
		String[] list = null;

		if (stringWriter.toString().indexOf("!DOCTYPE") != -1) {
			String[] tmpStrings = stringWriter.toString().split("\"");
			String tmpString = "";
			for (int i = 0; i < tmpStrings.length; i++) {
				if (tmpStrings[i].endsWith(".xml")) {
					tmpString = tmpString + tmpStrings[i] + "\n";
					System.out.println(tmpStrings[i]);
				}
			}
			System.out.println(tmpString);
			list = tmpString.split("\n");
		} else {
			System.out.println("Test");
			list = stringWriter.toString().split("\n");
		}*/
		
		
		System.out.println(list);
		levelList.setBounds(20, 20, 360, 140);
		levelList.setBorder(new BevelBorder(BevelBorder.RAISED, Color.black, Color.gray));
		levelList.setListData(list);
		levelList.addListSelectionListener(this);
		
		okayButton.setBounds(60, 170, 280, 30);
		okayButton.addActionListener(this);
		
		newLevelName.setMargin(new Insets(2, 2, 2, 2));
		newLevelName.setBounds(20, 220, 360, 50);
		newLevelName.setBorder(new BevelBorder(BevelBorder.RAISED, Color.black, Color.gray));
		newLevelName.setAutoscrolls(true);
		newLevelName.setLineWrap(true);
		
		newLevelButton.setBounds(60, 280, 280, 30);
		newLevelButton.addActionListener(this);
		
		levelDialog.add(levelList);
		levelDialog.add(okayButton);
		if (runEditor) {
			levelDialog.add(newLevelName);
			levelDialog.add(newLevelButton);
		} else {
			levelList.setSize(360, 200);
			okayButton.setLocation(60, 240);
		}
		
		levelDialog.setVisible(true);
	}
	
	private void hideLevelSelector() {
		levelDialog.dispose();
	}
	
	public void hideMenu() {
		menu.removeAll();
		menu.setVisible(false);
		Main.appletWindow.remove(menu);
	}
	
	public void destroyMenu() {
		menu.removeAll();
		Main.appletWindow.remove(menu);
		menu = null;
	}
	
	private void startGame() {
		hideMenu();
		Main.mainGame.start(runEditor, levelSelected);
	}
	
	private void showHelp() {
		hideMenu();
		helpMenu.show();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand() == "Play") {
			showLevelSelector(false);
		} else if (arg0.getActionCommand() == "Help") {
			showHelp();
		} else if (arg0.getActionCommand() == "Editor") {
			showLevelSelector(true);
		} else if (arg0.getActionCommand() == "Select") {
			if (tmpLevelSelected == null) return;
			levelSelected = tmpLevelSelected;
			hideLevelSelector();
			startGame();
		} else if (arg0.getActionCommand() == "Create") {
			try {
				DocumentBuilder newDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document newLevel = newDocBuilder.newDocument();
				newLevel.createElement("level");
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == true) return;
		JList list = (JList) e.getSource();
		tmpLevelSelected = list.getSelectedValue().toString();
	}
}
