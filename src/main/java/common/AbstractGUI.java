package common;

import instance.gui.LogTextAreaMouseListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.apache.commons.io.FileUtils;

import cloud.gui.SnapshotActionListener;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-16
 *
 */

public abstract class AbstractGUI extends JFrame implements GUI {
	
	private static final long serialVersionUID = 8057900512324630299L;
	private LogTextAreaMouseListener logTextAreaMouseListener = new LogTextAreaMouseListener(this);
	protected JTextArea logTextArea;
	protected JPanel logPanel;
	protected JScrollPane scrollPane;
	protected JTabbedPane tabbedPane;
	
	protected JMenuBar menuBar;
	protected JMenu fileMenu;
	protected JMenuItem exitItem;
	protected JMenu toolMenu;
	protected JMenuItem takeSnapshotMenuItem;
	protected SnapshotActionListener snapshotActionListener = new SnapshotActionListener(this);
	private boolean logTextLocked = false;
	private int lockedPosition;
	
	
	@Override
	public AbstractGUI getGUIComponent() {
		return this;
	}
	
	@Override
	public String getName() {
		return this.getTitle();
	}
	
	@Override
	public synchronized void log(String text) {
		synchronized (logTextArea) {
			logTextArea.append(text + "\n");
			if (!logTextLocked) logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
			else logTextArea.setCaretPosition(lockedPosition);
		}
	}
	
	@Override
	public void lockLogText() {
		lockedPosition = logTextArea.getDocument().getLength();
		logTextLocked = true;
	}
	
	@Override
	public void unlockLogText() {
		logTextLocked = false;
		synchronized (logTextArea) {
			logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
		}
	}
	
	@Override
	public void saveLogFileTo(File selectedDir) {
		saveLogTo(selectedDir, logTextArea.getText());		
	}

	protected void saveLogTo(File file, String text) {
		File logFile = new File(file.getPath() + File.separatorChar + getName() + "-log-" + System.currentTimeMillis() + ".log");
		try {
			FileUtils.writeStringToFile(logFile, text);
		} catch (IOException e) {
			log(e.getMessage());
		}
	}
	
	protected void createLogPanel() {
		logPanel = new JPanel();
		logPanel.setLayout(new GridLayout(1, 1));

		logTextArea = new JTextArea(5, 30);
		logTextArea.setEditable(false);
		logTextArea.setAutoscrolls(true);
		logTextArea.setFont(new Font("Courier New", Font.PLAIN, 11));
		logTextArea.setBackground(Color.DARK_GRAY);
		logTextArea.setForeground(Color.GREEN);
		logTextArea.addMouseListener(logTextAreaMouseListener);
		
		scrollPane = new JScrollPane(logTextArea);
		
		logPanel.add(scrollPane);
		tabbedPane.addTab("Log", logPanel);
		
	}
	
	protected void createMenuBar() {
		menuBar = new JMenuBar();
		
		createFileMenu();
		createToolMenu();
		
		setJMenuBar(menuBar);
		
	}

	private void createToolMenu() {
		toolMenu = new JMenu("Tools");
		menuBar.add(toolMenu);
		takeSnapshotMenuItem = new JMenuItem("Take snapshot");
		takeSnapshotMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		takeSnapshotMenuItem.addActionListener(snapshotActionListener);
		toolMenu.add(takeSnapshotMenuItem);
		
	}

	private void createFileMenu() {
		fileMenu = new JMenu("File");
		
		createFileMenuItems();
		
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		
		menuBar.add(fileMenu);
	}

	public abstract void createFileMenuItems();

	public abstract void takeSnapshot();


}
