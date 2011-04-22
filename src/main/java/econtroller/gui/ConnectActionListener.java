package econtroller.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */


public class ConnectActionListener implements ActionListener {

	private ControllerGUI gui;

	public ConnectActionListener(ControllerGUI gui) {
		this.gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (gui.validateConnectionParameter()) {
			gui.disableConnectionSection();
			gui.connectToCloudProvider();		
		}
	}

}
