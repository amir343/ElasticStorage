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
        gui.disableModeler();
		gui.startController();
	}

}