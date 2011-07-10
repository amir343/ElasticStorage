package cloud.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-08
 *
 */

public class InstanceTablePopupMenuListener extends MouseAdapter {

	private CloudGUI gui;
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem killInstanceItem;
	private JMenuItem restartInstanceItem;

	public InstanceTablePopupMenuListener(CloudGUI gui) {
		this.gui = gui;
		KillInstanceActionListener killInstanceActionListener = new KillInstanceActionListener(gui);
		killInstanceItem = new JMenuItem("Kill...");
		killInstanceItem.addActionListener(killInstanceActionListener);
		popupMenu.add(killInstanceItem);
		
		RestartInstanceActionListener restartInstanceActionListener = new RestartInstanceActionListener(gui);
		restartInstanceItem = new JMenuItem("Restart...");
		restartInstanceItem.addActionListener(restartInstanceActionListener);
		popupMenu.add(restartInstanceItem);
	}

	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		JTable table = gui.getInstances();
		int rowNumber = table.rowAtPoint( p );
		ListSelectionModel model = table.getSelectionModel();
		model.setSelectionInterval( rowNumber, rowNumber );
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
}
