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

import common.AbstractGUI;
import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.FrameDesign;
import instance.InstanceActor;
import instance.common.Block;
import instance.common.Size;
import instance.os.InstanceSnapshot;
import logger.Logger;
import logger.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class InstanceGUI extends AbstractGUI implements GenericInstanceGUI {
	
	private Logger logger = LoggerFactory.getLogger(InstanceGUI.class, this);
    private InstanceActor instanceActor;

    public static InstanceGUI getInstance() {
        return new InstanceGUI();
	}

	private static final long serialVersionUID = -444747445088218621L;

    private int totalNumberOfRequests = 0;

    private String nrRequestString;

    private JLabel currentTransfersLbl;

    private String currentTransfersString;

    private String requestQueueString;

    private JLabel requestQueueLbl;

    private JLabel simultaneousLabel;
    private JLabel nrRequests;
    private Radial cpuDialMeter;
    private DefaultTableModel model;
	private DefaultTableModel snapshotModel;
	private JTable dataTable;
	private JTable snapshotTable;
	private JPanel statTab;
	private JPanel dataSection;
	private JPanel cpuLoadPanel;
	private JPanel bandwidthPanel;
	private JPanel systemInfoPanel;
	private JPanel infoTab;
	private JPanel cpuLoadDiagramPanel;
	private JPanel snapshotSection;
	private JPanel snapshotTab;
	private ChartPanel cpuChartPanel;
	private ChartPanel bandwidthChartPanel;
	private JLabel cpuInfoLbl;
	private JLabel memoryLbl;
	private JLabel bandwidthInfoLbl;
	private String[] dataTableColumns = new String[]{"Name", "Size", "# Downloaders", "# Requested"};
	private String[] snapshotTableColumns = new String[]{"Snapshot ID", "Date"};
	private SnapshotTablePopupListener snapshotPopupListener = new SnapshotTablePopupListener(this);
	private RestartActionListener restartActionListener = new RestartActionListener(this);
	private List<InstanceSnapshot> snapshots = new ArrayList<InstanceSnapshot>();
	private JMenuItem restartMenuItem;
	private JLabel costLabel;

    public void setInstanceReference(InstanceActor actor) {
        this.instanceActor = actor;
    }

	public InstanceGUI() {
/*
        setUIManager();
*/
		createMenuBar();
		createTabs();
		addWindowListener();
		this.setSize(400,700);
		setupLocation();
		setVisible(true);
	}
	
	private void setupLocation() {
		this.setLocation(800, 0);
	}

	private void addWindowListener() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				instanceActor.stopActor();
			}
		});
	}
	
	private void createTabs() {
		tabbedPane = new JTabbedPane();
		createStatisticsTab();
		createInfoTab();
		createSnapshotTab();
		createLogPanel();
		setLayout(new GridLayout(1, 1));
		tabbedPane.setSelectedComponent(logPanel);
		add(tabbedPane);
	}

	private void createRestartMenuItem() {
		
		restartMenuItem = new JMenuItem("Restart Instance");
		restartMenuItem.addActionListener(restartActionListener);
		
		fileMenu.add(restartMenuItem);
	}

	private void createSnapshotTab() {
		snapshotTab = new JPanel();
		snapshotTab.setLayout(new GridLayout(1,1));
		
		createSnapshotSection();
		
		tabbedPane.addTab("Snapshots", snapshotTab);
	}

	private void createSnapshotSection() {
		snapshotSection = new JPanel();
		snapshotSection.setLayout(new BorderLayout());
		snapshotSection.setBorder(BorderFactory.createTitledBorder("Snapshot(s)"));
		
		createSnapshotTable();
		
		snapshotTab.add(snapshotSection);
	}

	private void createSnapshotTable() {
		snapshotModel = new DefaultTableModel(new String[][]{}, snapshotTableColumns);
		snapshotTable = new JTable(snapshotModel){
			private final long serialVersionUID = 6454534842446167244L;
			public boolean isCellEditable(int rowIndex, int colIndex) {
		          return false;
	        }
		};
		
		snapshotTable.addMouseListener(snapshotPopupListener);
		snapshotTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		JScrollPane pane = new JScrollPane(snapshotTable);
		snapshotSection.add(pane);
	}

	private void createInfoTab() {
		infoTab = new JPanel();
		infoTab.setLayout(new GridLayout(2,0));
		
		createSystemInfoPanel();
		createDataSection();
		
		tabbedPane.addTab("System Info", infoTab);
	}

	private void createSystemInfoPanel() {
		systemInfoPanel = new JPanel();
		systemInfoPanel.setBorder(BorderFactory.createTitledBorder("System Info"));
        systemInfoPanel.setLayout(new BoxLayout(systemInfoPanel, BoxLayout.Y_AXIS));

		cpuInfoLbl = new JLabel("CPU: ");
		cpuInfoLbl.setBorder(BorderFactory.createEtchedBorder());
		memoryLbl = new JLabel("Memory: ");
		memoryLbl.setBorder(BorderFactory.createEtchedBorder());
		bandwidthInfoLbl = new JLabel("Bandwidth: ");
		bandwidthInfoLbl.setBorder(BorderFactory.createEtchedBorder());
		costLabel = new JLabel("Cost: $ 0.0");
		costLabel.setBorder(BorderFactory.createEtchedBorder());
        simultaneousLabel = new JLabel("Simultaneous Downloads: ");
        simultaneousLabel.setBorder(BorderFactory.createEtchedBorder());
		
		systemInfoPanel.add(cpuInfoLbl);
		systemInfoPanel.add(memoryLbl);
		systemInfoPanel.add(bandwidthInfoLbl);
		systemInfoPanel.add(costLabel);
		systemInfoPanel.add(simultaneousLabel);

		infoTab.add(systemInfoPanel);

	}

	private void createStatisticsTab() {
		statTab = new JPanel();
		statTab.setLayout(new GridLayout(3, 0));
		
		createCPUPanel();
		createCPULoadDiagramPanel();
		createBandwidthPanel();
		
		statTab.add(cpuLoadPanel);
		statTab.add(cpuLoadDiagramPanel);
		statTab.add(bandwidthPanel);
		tabbedPane.addTab("Statistics", statTab);		
	}

	private void createCPULoadDiagramPanel() {
		cpuLoadDiagramPanel = new JPanel();
		cpuLoadDiagramPanel.setBorder(BorderFactory.createTitledBorder("CPU Load"));
		cpuLoadDiagramPanel.setLayout(new GridLayout(1,0));
	}

	private void createBandwidthPanel() {
		bandwidthPanel = new JPanel();
		bandwidthPanel.setBorder(BorderFactory.createTitledBorder("Bandwidth"));
		bandwidthPanel.setLayout(new GridLayout());		
	}

	private void createDataSection() {
		dataSection = new JPanel();
		dataSection.setBorder(BorderFactory.createTitledBorder("Data block(s)"));
		dataSection.setLayout(new BoxLayout(dataSection, BoxLayout.Y_AXIS));

        createNumberOfRequestsLabel(dataSection);
        createCurrentTransfers(dataSection);
        createRequestQueue(dataSection);
		createDataBlockTable(dataSection);

		infoTab.add(dataSection);
	}

    private void createRequestQueue(JPanel dataSection) {
        requestQueueString = "Request Queue: ";
        requestQueueLbl = new JLabel(requestQueueString + "0", JLabel.CENTER);
        dataSection.add(requestQueueLbl);
    }

    private void createCurrentTransfers(JPanel dataSection) {
        currentTransfersString = "Current Transfers: ";
        currentTransfersLbl = new JLabel(currentTransfersString + 0, JLabel.CENTER);
        dataSection.add(currentTransfersLbl);
    }

    private void createNumberOfRequestsLabel(JPanel dataSection) {
        nrRequestString = "Total number of Requests: ";
        nrRequests = new JLabel(nrRequestString + totalNumberOfRequests, JLabel.CENTER);
        dataSection.add(nrRequests);
    }

    private void createCPUPanel() {
		cpuLoadPanel = new JPanel();
		cpuLoadPanel.setBorder(BorderFactory.createTitledBorder("Current CPU Load"));

        try {
            cpuDialMeter = new Radial();
            cpuDialMeter.init(70, 70);
            cpuDialMeter.setTitle("CPU Load");
            cpuDialMeter.setUnitString("");
            cpuDialMeter.setMaximumSize(new Dimension(40, 40));
            cpuDialMeter.setCustomTickmarkLabelsEnabled(false);
            cpuDialMeter.setMinValue(0.0);
            cpuDialMeter.setMaxValue(200);
            cpuDialMeter.setValue(5);
            cpuDialMeter.setThreshold(70.0);
            cpuDialMeter.setFrameDesign(FrameDesign.CHROME);
            cpuDialMeter.setBackgroundColor(BackgroundColor.BEIGE);
            cpuDialMeter.setLcdDecimals(1);
            cpuLoadPanel.add(cpuDialMeter);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
	}

	private void createDataBlockTable(JPanel dataSec) {
		model = new DefaultTableModel(new String[][]{}, dataTableColumns );
		dataTable = new JTable(model){
			private final long serialVersionUID = 6454534842446167244L;
			public boolean isCellEditable(int rowIndex, int colIndex) {
		          return false;
	        }
		};
		JScrollPane pane = new JScrollPane(dataTable);
		dataSec.add(pane);		
	}

    @Override
	public void cpuLoad(double load) {
		cpuDialMeter.setLcdValue(load);
		cpuDialMeter.setValueAnimated(load);
	}

    @Override
	public void initializeDataBlocks(List<Block> blocks) {
		if (blocks != null & model.getRowCount() == 0) {
            for (Block block : blocks) {
				if (StringUtils.isNotEmpty(block.name())) {
					model.insertRow(model.getRowCount(), new Object[]{block.name(), Size.getSizeString(block.size()), 0, 0});
				} else {
					logger.error("Block name can not be null");
				}
			}
		} else {
			if (blocks == null) logger.error("blocks can not be null");
		}
	}

    @Override
	public synchronized void increaseNrDownloadersFor(String blockId) {
		if (StringUtils.isNotEmpty(blockId)) {
            totalNumberOfRequests++;
            nrRequests.setText(nrRequestString + totalNumberOfRequests);
            nrRequests.revalidate();
			for (int i=0; i< model.getRowCount(); i++) {
				if ( ((String)model.getValueAt(i, 0)).equals(blockId)) {
					int currentValue = (Integer) model.getValueAt(i, 2);
					currentValue ++;
					model.setValueAt(currentValue, i, 2);
					currentValue = (Integer) model.getValueAt(i, 3);
					currentValue ++;				
					model.setValueAt(currentValue, i, 3);
					break;
				}
			}
		} else {
			logger.error("blockID can not be null");
		}
	}

    @Override
	public synchronized void decreaseNrDownloadersFor(String blockId) {
		if (!StringUtils.isEmpty(blockId)) {
			for (int i=0; i< model.getRowCount(); i++) {
				if ( ((String)model.getValueAt(i, 0)).equals(blockId)) {
					int currentValue = (Integer) model.getValueAt(i, 2);
					currentValue --;
					if (currentValue < 0 ) currentValue = 0;
					model.setValueAt(currentValue, i, 2);
					break;
				}
			}
		} else {
			logger.error("blockID can not be null");
		}
	}

    @Override
	public synchronized void resetNrDownloaders() {
		for (int i=0; i< model.getRowCount(); i++) {
			model.setValueAt(0, i, 2);
			model.setValueAt(0, i, 3);
		}
	}
	
	@Override
    public void createCPULoadDiagram(JFreeChart chart) {
		if (cpuChartPanel != null)
			cpuLoadDiagramPanel.remove(cpuChartPanel);
		cpuChartPanel = new ChartPanel(chart);
		cpuLoadDiagramPanel.add(cpuChartPanel);
		cpuLoadDiagramPanel.revalidate();
	}

    @Override
	public void createBandwidthDiagram(JFreeChart chart) {
		if (null != chart) {
			if (bandwidthChartPanel != null) 
				bandwidthPanel.remove(bandwidthChartPanel);
			bandwidthChartPanel = new ChartPanel(chart);
			bandwidthPanel.add(bandwidthChartPanel);
			bandwidthPanel.revalidate();
		} else {
			logger.error("chart can not be null");
		}
	}

    @Override
	public void updateCPUInfoLabel(String info) {
		cpuInfoLbl.setText("CPU: " + info);
		cpuInfoLbl.revalidate();
	}

    @Override
	public void updateMemoryInfoLabel(String info) {
		memoryLbl.setText("Memory: " + info);
		memoryLbl.revalidate();
	}

    @Override
	public void updateBandwidthInfoLabel(String info) {
		bandwidthInfoLbl.setText("Bandwidth: " + info);
		bandwidthInfoLbl.revalidate();
	}

    @Override
	public void updateSimultaneousDownloads(String info) {
		simultaneousLabel.setText("Simultaneous Downloads: " + info);
		simultaneousLabel.revalidate();
	}

    @Override
	public void takeSnapshot() {
		instanceActor.takeSnapshot();
	}

    @Override
	public void addSnapshot(InstanceSnapshot snapshot) {
		if (null != snapshot) {
			snapshotModel.insertRow(snapshotModel.getRowCount(), new Object[]{snapshot.getId(), snapshot.getDate()});
			snapshot.addLogText(logTextArea.getText());
			snapshots.add(snapshot);
		} else {
			logger.error("snapshot can not be null");
		}
	}

    @Override
	public JTable getSnapshotTable() {
		return snapshotTable;
	}

    @Override
	public void deleteAllSnapshots() {
		snapshots.clear();
		for (int i=snapshotModel.getRowCount()-1; i>=0; i--)
			snapshotModel.removeRow(i);		
	}

    @Override
	public void saveAllSnapshotsTo(File selectedDir) {
		for (InstanceSnapshot snapshot : snapshots) {
			saveSnapshotTo(selectedDir, snapshot);
		}		
	}

	@Override
	public void saveSelectedSnapshotTo(File selectedDir) {
		int id = (Integer) snapshotModel.getValueAt(snapshotTable.getSelectedRow(), 0);
		InstanceSnapshot snapshot = getSnapshotWithId(id);
		saveSnapshotTo(selectedDir, snapshot);
	}
	
	private void saveSnapshotTo(File selectedFile, InstanceSnapshot snapshot) {
		File nodeDir = new File(selectedFile.getPath() + File.separatorChar + this.getTitle());
		if (!nodeDir.exists()) nodeDir.mkdir();
		File snapshotDir = new File(nodeDir.getPath() + File.separatorChar + snapshot.getId());
		if (!snapshotDir.exists()) {
			snapshotDir.mkdir();
		
			writePNG(snapshot.getCpuChart(), snapshotDir, "CpuLoadDiagram.png");
			writePNG(snapshot.getBandwidthChart(), snapshotDir, "BandwidthDiagram.png");
			saveLogTo(snapshotDir, snapshot.getLog());
		}
	}

	private InstanceSnapshot getSnapshotWithId(int id) {
		for (InstanceSnapshot snapshot : snapshots) {
			if (snapshot.getId() == id)
				return snapshot;
		}
		return null;
	}

    @Override
	public void systemRestart() {
		resetNrDownloaders();
	}

    @Override
	public void restartOS() {
		instanceActor.restartInstance();
		cpuDialMeter.setValueAnimated(0.0);
	}
	
	@Override
	public void createFileMenuItems() {
		createRestartMenuItem();		
	}

    @Override
	public void decorateWhileSystemStartUp() {
		tabbedPane.setEnabledAt(0, false);
		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);
		toolMenu.setEnabled(false);
		fileMenu.setEnabled(false);
        tabbedPane.setSelectedComponent(logPanel);
	}

    @Override
	public void decorateSystemStarted() {
		tabbedPane.setEnabledAt(0, true);
		tabbedPane.setEnabledAt(1, true);
		tabbedPane.setEnabledAt(2, true);
		toolMenu.setEnabled(true);
		fileMenu.setEnabled(true);
		tabbedPane.setSelectedComponent(statTab);
	}

    @Override
	public void updateCurrentCost(String cost) {
		costLabel.setText("Cost: $ " + cost);
		costLabel.revalidate();		
	}

    @Override
    public void updateTitle(String title) {
        this.setTitle(title);
    }

    @Override
    public synchronized void updateCurrentTransfers(int size) {
        currentTransfersLbl.setText(currentTransfersString + size);
    }

    @Override
    public synchronized void updateRequestQueue(int n) {
        requestQueueLbl.setText(requestQueueString + n);
    }

}
