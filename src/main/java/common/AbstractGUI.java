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

import cloud.gui.SnapshotActionListener;
import instance.gui.LogTextAreaMouseListener;
import org.apache.commons.io.FileUtils;
import org.jfree.chart.JFreeChart;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-16
 *
 */

public abstract class AbstractGUI extends JFrame implements GUI {
	
	private final long serialVersionUID = 8057900512324630299L;
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
    protected FilterActionListener filterActionListner = new FilterActionListener(this);
	private AtomicBoolean logTextLocked = new AtomicBoolean(false);
	private int lockedPosition;
    private JTextField filterTxt;
    private JButton filterBtn;
    private List<String> logLines = new ArrayList<String>();
    private boolean filterMode = false;
    private String filterKeyword = "";

    @Override
	public AbstractGUI getGUIComponent() {
		return this;
	}
	
	@Override
	public String getName() {
		return this.getTitle();
	}
	
	@Override
	public void log(String text) {
        logLines.add(text + "\n");
        if (filterMode) {
            if (text.toLowerCase().contains(filterKeyword))
                logTextArea.append(text + "\n");
        } else {
            logTextArea.append(text + "\n");
        }
		if (!logTextLocked.get()) logTextArea.setCaretPosition(logTextArea.getText().length());
		else logTextArea.setCaretPosition(lockedPosition);
	}
	
	@Override
	public void lockLogText() {
		lockedPosition = logTextArea.getText().length();
		logTextLocked.set(true);
	}
	
	@Override
	public void unlockLogText() {
		logTextLocked.set(false);
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
	
	protected void writePNG(JFreeChart jFreeChart, File snapshotDir, String name) {
		if (jFreeChart != null) {
			BufferedImage cpuImage = jFreeChart.createBufferedImage(833, 500);
			File cpuFile = new File(snapshotDir.getPath() + File.separatorChar + name);
			try {
				ImageIO.write(cpuImage, "PNG", cpuFile);
			} catch (IOException e) {
				log(e.getMessage());
			}
		}
	}
	
	protected void createLogPanel() {
		logPanel = new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

		logTextArea = new JTextArea(33, 30);
		logTextArea.setEditable(false);
		logTextArea.setAutoscrolls(true);
		logTextArea.setFont(new Font("Courier New", Font.PLAIN, 11));
		logTextArea.setBackground(Color.DARK_GRAY);
		logTextArea.setForeground(Color.GREEN);
		logTextArea.addMouseListener(logTextAreaMouseListener);
		
		scrollPane = new JScrollPane(logTextArea);

        Box horizontalLayout = Box.createHorizontalBox();
        filterTxt = new JTextField();
        filterBtn = new JButton("Filter");
        filterBtn.addActionListener(filterActionListner);
        filterTxt.setMaximumSize(new Dimension(250, 30));
        horizontalLayout.add(filterTxt);
        horizontalLayout.add(filterBtn);
        logPanel.add(horizontalLayout);
		logPanel.add(scrollPane);
		tabbedPane.addTab("Log", logPanel);
	}

    public void filter() {
        String keyword = filterTxt.getText();
        if (keyword != null && !keyword.trim().equals("")) {
            filterMode = true;
            filterKeyword = keyword.toLowerCase();
            synchronized (logTextArea) {
                logTextArea.setText("");
                for (String line : logLines) {
                    if (line.toLowerCase().contains(filterKeyword))
                        logTextArea.append(line);
                }
            }
        } else {
            if (filterMode) {
                filterMode = false;
                synchronized (logTextArea) {
                    logTextArea.setText("");
                    for (String line : logLines) {
                        logTextArea.append(line);
                    }
                }
            }
        }
    }

    protected void setUIManager() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }
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

	public abstract void saveAllSnapshotsTo(File selectedFile);

	public abstract void deleteAllSnapshots();

	public abstract JTable getSnapshotTable();


}
