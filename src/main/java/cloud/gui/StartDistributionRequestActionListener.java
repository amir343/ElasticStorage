package cloud.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class StartDistributionRequestActionListener implements ActionListener {

	private CloudGUI gui;

	public StartDistributionRequestActionListener(CloudGUI cloudGUI) {
		this.gui = cloudGUI;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.startDistributionBehaviour();
	}

}
