package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cloud.gui.CloudGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class DeleteSnapshotActionListener implements ActionListener {

	private InstanceGUI instanceGui;
	private CloudGUI cloudGui;
	private boolean instance;
	
	public DeleteSnapshotActionListener(InstanceGUI gui) {
		this.instanceGui = gui;
		this.instance = true;
	}

	public DeleteSnapshotActionListener(CloudGUI gui) {
		this.cloudGui = gui;
		this.instance = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (instance)
			instanceGui.deleteAllSnapshots();
		else
			cloudGui.deleteAllSnapshots();
	}

}
