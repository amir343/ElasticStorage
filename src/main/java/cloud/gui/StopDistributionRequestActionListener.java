package cloud.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class StopDistributionRequestActionListener implements ActionListener {

	private CloudGUI gui;

	public StopDistributionRequestActionListener(CloudGUI gui) {
		this.gui = gui;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		gui.stopDistributionBehaviour();
	}

}
