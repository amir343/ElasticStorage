package instance.gui;

import instance.common.Block;
import instance.common.Size;
import instance.os.InstanceSnapshot;
import instance.os.OS;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import logger.Logger;
import logger.LoggerFactory;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import common.AbstractGUI;

import eu.hansolo.steelseries.gauges.Radial;
import eu.hansolo.steelseries.tools.BackgroundColor;
import eu.hansolo.steelseries.tools.FrameDesign;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class InstanceGUI extends AbstractGUI {
	
	private static InstanceGUI instance = new InstanceGUI();
	private Logger logger = LoggerFactory.getLogger(InstanceGUI.class, this);
	
	public static InstanceGUI getInstance() {
		return instance;
	}
	
	private static final long serialVersionUID = -444747445088218621L;
	private Radial cpuDialMeter;
	private DefaultTableModel model;
	private DefaultTableModel snapshotModel;
	private JTable dataTable;
	private JTable snapshotTable;
	private JPanel statPanel;
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
	private OS os;
	private List<InstanceSnapshot> snapshots = new ArrayList<InstanceSnapshot>();
	private JMenuItem restartMenuItem;
	
	public InstanceGUI() {
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
				System.exit(0); //
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
			private static final long serialVersionUID = 6454534842446167244L;
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
		systemInfoPanel.setLayout(new FlowLayout());
		systemInfoPanel.setBorder(BorderFactory.createTitledBorder("System Info"));
		
		cpuInfoLbl = new JLabel("CPU: ");
		cpuInfoLbl.setBorder(BorderFactory.createEtchedBorder());
		memoryLbl = new JLabel("Memory: ");
		memoryLbl.setBorder(BorderFactory.createEtchedBorder());
		bandwidthInfoLbl = new JLabel("Bandwidth: ");
		bandwidthInfoLbl.setBorder(BorderFactory.createEtchedBorder());
		
		systemInfoPanel.add(cpuInfoLbl);
		systemInfoPanel.add(memoryLbl);
		systemInfoPanel.add(bandwidthInfoLbl);
		
		infoTab.add(systemInfoPanel);

	}

	private void createStatisticsTab() {
		statPanel = new JPanel();
		statPanel.setLayout(new GridLayout(3, 0));
		
		createCPUPanel();
		createCPULoadDiagramPanel();
		createBandwidthPanel();
		
		statPanel.add(cpuLoadPanel);
		statPanel.add(cpuLoadDiagramPanel);
		statPanel.add(bandwidthPanel);
		tabbedPane.addTab("Statistics", statPanel);		
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
		dataSection.setLayout(new GridLayout());
		createDataBlockTable(dataSection);
		infoTab.add(dataSection);
	}

	private void createCPUPanel() {
		cpuLoadPanel = new JPanel();
		cpuLoadPanel.setBorder(BorderFactory.createTitledBorder("Current CPU Load"));
		
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
		cpuLoadPanel.add(cpuDialMeter);
	}

	private void createDataBlockTable(JPanel dataSec) {
		model = new DefaultTableModel(new String[][]{}, dataTableColumns );
		dataTable = new JTable(model){
			private static final long serialVersionUID = 6454534842446167244L;
			public boolean isCellEditable(int rowIndex, int colIndex) {
		          return false;
	        }
		};
		JScrollPane pane = new JScrollPane(dataTable);
		dataSec.add(pane);		
	}

	public void cpuLoad(double load) {
		cpuDialMeter.setValueAnimated(load);
	}
	
	public void initializeDataBlocks(List<Block> blocks) {
		if (blocks != null) {
			for (Block block : blocks) {
				if (StringUtils.isNotEmpty(block.getName())) {
					model.insertRow(model.getRowCount(), new Object[]{block.getName(), Size.getSizeString(block.getSize()), 0, 0});
				} else {
					logger.error("Block name can not be null");
				}
			}
		} else {
			logger.error("blocks can not be null");
		}
	}

	public synchronized void increaseNrDownloadersFor(String blockId) {
		if (StringUtils.isNotEmpty(blockId)) {
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
			logger.error("blockId can not be null");
		}
	}

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
			logger.error("blockId can not be null");
		}
	}

	public synchronized void resetNrDownloaders() {
		for (int i=0; i< model.getRowCount(); i++) {
			model.setValueAt(0, i, 2);
			model.setValueAt(0, i, 3);
		}
	}
	
	public void createCPULoadDiagram(JFreeChart chart) {
		if (cpuChartPanel != null)
			cpuLoadDiagramPanel.remove(cpuChartPanel);
		cpuChartPanel = new ChartPanel(chart);
		cpuLoadDiagramPanel.add(cpuChartPanel);
		cpuLoadDiagramPanel.revalidate();
	}
	
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
	
	public void updateCPUInfoLabel(String info) {
		cpuInfoLbl.setText("CPU: " + info);
		cpuInfoLbl.revalidate();
	}
	
	public void updateMemoryInfoLabel(String info) {
		memoryLbl.setText("Memory: " + info);
		memoryLbl.revalidate();
	}
	
	public void updateBandwidthInfoLabel(String info) {
		bandwidthInfoLbl.setText("Bandwidth: " + info);
		bandwidthInfoLbl.revalidate();
	}
	
	public void setOSReference(OS os) {
		this.os = os;
	}

	public void takeSnapshot() {
		os.takeSnapshot();
	}

	public void addSnapshot(InstanceSnapshot snapshot) {
		if (null != snapshot) {
			snapshotModel.insertRow(snapshotModel.getRowCount(), new Object[]{snapshot.getId(), snapshot.getDate()});
			snapshot.addLogText(logTextArea.getText());
			snapshots.add(snapshot);
		} else {
			logger.error("snapshot can not be null");
		}
	}

	public JTable getSnapshotTable() {
		return snapshotTable;
	}

	public void deleteAllSnapshots() {
		snapshots.clear();
		for (int i=snapshotModel.getRowCount()-1; i>=0; i--)
			snapshotModel.removeRow(i);		
	}

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
		
			writeCPULoadDiagram(snapshot, snapshotDir);
			writeBandwidthDiagram(snapshot, snapshotDir);
			saveLogTo(snapshotDir, snapshot.getLog());
		}
	}

	private void writeBandwidthDiagram(InstanceSnapshot snapshot, File snapshotDir) {
		if (snapshot.getBandwidthChart() != null) {
			BufferedImage bandwidthImage = snapshot.getBandwidthChart().createBufferedImage(833, 500);
			File bandwidthFile = new File(snapshotDir.getPath() + File.separatorChar + "BandwidthDiagram.png");
			try {
				ImageIO.write(bandwidthImage, "PNG", bandwidthFile);
			} catch (IOException e) {
				log(e.getMessage());
			}			
		}
	}

	private void writeCPULoadDiagram(InstanceSnapshot snapshot, File snapshotDir) {
		if (snapshot.getCpuChart() != null) {
			BufferedImage cpuImage = snapshot.getCpuChart().createBufferedImage(833, 500);
			File cpuFile = new File(snapshotDir.getPath() + File.separatorChar + "CpuLoadDiagram.png");
			try {
				ImageIO.write(cpuImage, "PNG", cpuFile);
			} catch (IOException e) {
				log(e.getMessage());
			}
		}
	}
	
	private InstanceSnapshot getSnapshotWithId(int id) {
		for (InstanceSnapshot snapshot : snapshots) {
			if (snapshot.getId() == id)
				return snapshot;
		}
		return null;
	}

	public void systemRestart() {
		resetNrDownloaders();		
	}

	public void restartOS() {
		os.restartInstance();		
		cpuDialMeter.setValueAnimated(0.0);
	}
	
	public static void main(String[] aregs) {
		InstanceGUI.getInstance();
	}

	@Override
	public void createFileMenuItems() {
		createRestartMenuItem();		
	}
	 
}
