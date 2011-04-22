package econtroller.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-19
 *
 */

public class StopControllerActionListener implements ActionListener {

	private ControllerGUI gui;

	public StopControllerActionListener(ControllerGUI controllerGUI) {
		this.gui = controllerGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.enableControllerDesignSection();
		gui.stopController();
	}

}
