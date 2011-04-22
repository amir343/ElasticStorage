package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-15
 *
 */

public class RestartActionListener implements ActionListener {

	private InstanceGUI gui;

	public RestartActionListener(InstanceGUI instanceGUI) {
		this.gui = instanceGUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.restartOS();		
	}

}
