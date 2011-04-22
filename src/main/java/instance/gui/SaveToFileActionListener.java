package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import common.GUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-16
 *
 */

public class SaveToFileActionListener implements ActionListener {

	private GUI gui;

	public SaveToFileActionListener(GUI gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle("Please choose the directory to save log file");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(gui.getGUIComponent()) == JFileChooser.APPROVE_OPTION) {
			gui.saveLogFileTo(fileChooser.getSelectedFile());
		}
		
	}

}
