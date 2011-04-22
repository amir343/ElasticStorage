package econtroller.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class DisconnectActionListener implements ActionListener {

	private ControllerGUI gui;

	public DisconnectActionListener(ControllerGUI controllerGUI) {
		this.gui = controllerGUI;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.disconnectFromCloudProvider();
	}

}
