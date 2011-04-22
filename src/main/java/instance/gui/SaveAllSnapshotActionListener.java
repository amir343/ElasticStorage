package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import cloud.gui.CloudGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class SaveAllSnapshotActionListener implements ActionListener {

	private InstanceGUI instanceGui;
	private String choosertitle = "Choose where to save all snapshots";
	private CloudGUI cloudGui;
	private boolean instance;
	
	public SaveAllSnapshotActionListener(InstanceGUI gui) {
		this.instanceGui = gui;
		this.instance = true;
	}

	public SaveAllSnapshotActionListener(CloudGUI gui) {
		this.cloudGui = gui;
		this.instance = false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File("."));
		fileChooser.setDialogTitle(choosertitle);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showOpenDialog(instanceGui) == JFileChooser.APPROVE_OPTION) {
			if (instance)
				instanceGui.saveAllSnapshotsTo(fileChooser.getSelectedFile());
			else
				cloudGui.saveAllSnapshotsTo(fileChooser.getSelectedFile());
		}

	}

}
