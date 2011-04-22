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
