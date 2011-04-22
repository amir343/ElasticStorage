package cloud.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-26
 *
 */

public class RemoveInstanceActionListener implements ActionListener {

	private CloudGUI gui;

	public RemoveInstanceActionListener(CloudGUI gui) {
		this.gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		gui.killSelectedInstances();
	}

}
