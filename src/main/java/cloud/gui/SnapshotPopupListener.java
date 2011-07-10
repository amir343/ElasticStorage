package cloud.gui;

import instance.gui.DeleteSnapshotActionListener;
import instance.gui.SaveAllSnapshotActionListener;
import instance.gui.SaveSnapshotActionListener;

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

public class SnapshotPopupListener extends MouseAdapter {
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem saveMenuItem;
	private JMenuItem saveAllMenuItem;
	private JMenuItem deleteMenuItem;
	private CloudGUI gui;
	
	public SnapshotPopupListener(CloudGUI gui) {
		this.gui = gui;
		SaveSnapshotActionListener saveSnapshotActionListener = new SaveSnapshotActionListener(gui);
		SaveAllSnapshotActionListener saveAllSnapshotActionListener = new SaveAllSnapshotActionListener(gui);
		DeleteSnapshotActionListener deleteSnapshotActionListener = new DeleteSnapshotActionListener(gui);
		saveMenuItem = new JMenuItem("Save...");
		saveMenuItem.addActionListener(saveSnapshotActionListener);
		saveAllMenuItem = new JMenuItem("Save all...");
		saveAllMenuItem.addActionListener(saveAllSnapshotActionListener);
		deleteMenuItem = new JMenuItem("Delete all");
		deleteMenuItem.addActionListener(deleteSnapshotActionListener);
		popupMenu.add(saveMenuItem);
		popupMenu.add(saveAllMenuItem);
		popupMenu.add(deleteMenuItem);
	}
	
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		JTable table = gui.getSnapshotTable();
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
