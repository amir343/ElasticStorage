package instance.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-21
 *
 */

public class LockActionListener implements ActionListener {

	private LogTextAreaMouseListener logTextAreaMouseListener;
	private boolean lock = true;

	public LockActionListener(LogTextAreaMouseListener logTextAreaMouseListener) {
		this.logTextAreaMouseListener = logTextAreaMouseListener;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (lock) {
			logTextAreaMouseListener.changeToUnlock();
			lock = false;
		} else {
			logTextAreaMouseListener.changeToLock();
			lock = true;
		}
		
	}

}
