package instance.gui;

import common.AbstractGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class SaveAllSnapshotActionListener implements ActionListener {

	private String choosertitle = "Choose where to save all snapshots";
	private AbstractGUI gui;
	
	public SaveAllSnapshotActionListener(AbstractGUI gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle(choosertitle);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(gui) == JFileChooser.APPROVE_OPTION)
			gui.saveAllSnapshotsTo(fileChooser.getSelectedFile());

	}

}
