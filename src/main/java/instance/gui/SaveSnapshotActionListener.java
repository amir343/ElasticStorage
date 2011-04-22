package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import common.GUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class SaveSnapshotActionListener implements ActionListener {

	private String choosertitle = "Select where to save the Snapshot";
	private GUI gui;
	
	public SaveSnapshotActionListener(GUI gui) {
		this.gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle(choosertitle);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(gui.getGUIComponent()) == JFileChooser.APPROVE_OPTION) {
			gui.saveSelectedSnapshotTo(fileChooser.getSelectedFile());
		}

	}

}
