package econtroller.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-19
 *
 */

public class StartControllerActionListener implements ActionListener {

	private ControllerGUI gui;

	public StartControllerActionListener(ControllerGUI controllerGUI) {
		this.gui = controllerGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.disableControllerDesignSection();
		gui.startController();
	}

}
