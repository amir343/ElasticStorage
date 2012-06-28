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
package common;

import java.io.File;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 * A common interface for all GUIs in the system
 *
 *
 */

public interface GUI {

	public AbstractGUI getGUIComponent();
	
	public void log(String text);

	public void saveLogFileTo(File selectedDir);

	public void saveSelectedSnapshotTo(File selectedFile);

	public void lockLogText();

	public void unlockLogText();

}
