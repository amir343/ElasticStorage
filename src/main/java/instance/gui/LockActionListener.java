/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
