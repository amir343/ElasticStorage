package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class SnapshotActionListener implements ActionListener {

	private InstanceGUI gui;

	public SnapshotActionListener(InstanceGUI instanceGUI) {
		this.gui = instanceGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.takeSnapshot();
	}

}
