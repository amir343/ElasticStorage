package cloud.gui;

import common.AbstractGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class SnapshotActionListener implements ActionListener {

	private AbstractGUI gui;

	public SnapshotActionListener(AbstractGUI abstractGUI) {
		this.gui = abstractGUI;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		gui.takeSnapshot();
	}
	
}
