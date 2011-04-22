package cloud.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import common.AbstractGUI;

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
