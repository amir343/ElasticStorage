package cloud.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class KillInstanceActionListener implements ActionListener {

	private CloudGUI gui;

	public KillInstanceActionListener(CloudGUI gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.killSelectedInstances();
	}

}
