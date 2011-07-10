package instance.gui;

import common.GUI;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * @author Amir Moulavi
 *
 */

public class LogTextAreaMouseListener extends MouseAdapter {

	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem saveToFileMenuItem;
	private JMenuItem lockMenuItem;
	private LockActionListener lockActionListener;
	private GUI gui;

	public LogTextAreaMouseListener(GUI gui) {
		this.gui = gui;
		lockActionListener = new LockActionListener(this);
		lockMenuItem = new JMenuItem("Lock");
		lockMenuItem.addActionListener(lockActionListener);
		popupMenu.add(lockMenuItem);		
		
		SaveToFileActionListener saveToFileActionListener = new SaveToFileActionListener(gui);
		saveToFileMenuItem = new JMenuItem("Save to file...");
		saveToFileMenuItem.addActionListener(saveToFileActionListener);
		popupMenu.add(saveToFileMenuItem);
		
	}
	
	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}
		    
	private void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void changeToUnlock() {
		lockMenuItem.setText("Unlock");		
		lockMenuItem.repaint();
		gui.lockLogText();
	}

	public void changeToLock() {
		lockMenuItem.setText("Lock");		
		lockMenuItem.repaint();
		gui.unlockLogText();
	}

}
