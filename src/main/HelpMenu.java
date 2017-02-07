package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HelpMenu implements ActionListener, ItemListener {

	JPanel panel;

	public HelpMenu() {
		panel = new JPanel();
		panel.setLayout(null);
		panel.setVisible(false);
	}

	public void show() {
		Main.appletWindow.add(panel);
		JLabel textArea = new JLabel();
		textArea.setText("<html><font size=5><p align=center>Version "
				+ Main.MAJOR_VERSION
				+ "."
				+ Main.MINOR_VERSION
				+ "."
				+ Main.REVISION_NUMBER
				+ "</font><font size=10><br/><br/><b>Help Menu</b><br/>"
				+ "W A S D to move the filled in box.<br/>"
				+ "Click to create rope.<br/>"
				+ "C to change camera view.<br/>"
				+ "R to reset level.</p></font></html>");
		textArea.setBackground(panel.getBackground());
		textArea.setBounds(Main.SCREEN_WIDTH / 2 - 300, 0, 800, 300);
		JButton backButton = new JButton("Back");
		backButton.addActionListener(this);
		backButton.setFont(Menu.menuFont);
		backButton.setBounds(Main.SCREEN_WIDTH / 2 - 100, 320, 200, 80);
		JCheckBox debugBox = new JCheckBox("Debug Mode",
				Main.mainGame.debugMode);
		debugBox.addItemListener(this);
		debugBox.setFont(Menu.menuFont);
		debugBox.setBounds(Main.SCREEN_WIDTH / 2 - 100, 420, 200, 80);
		panel.add(textArea);
		panel.add(backButton);
		panel.add(debugBox);
		panel.setVisible(true);
	}

	private void hide() {
		panel.removeAll();
		panel.setVisible(false);

		Main.appletWindow.remove(panel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Back") {
			hide();
			Main.mainMenu.showMenu();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		int state = e.getStateChange();
		if (state == ItemEvent.SELECTED) {
			Main.mainGame.debugMode = true;
		} else {
			Main.mainGame.debugMode = false;
		}
	}
}
