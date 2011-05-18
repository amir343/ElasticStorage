package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import common.AbstractGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class DeleteSnapshotActionListener implements ActionListener {

	private AbstractGUI gui;

	public DeleteSnapshotActionListener(AbstractGUI gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
			gui.deleteAllSnapshots();
	}

}
