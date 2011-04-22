package cloud.gui;

import java.awt.BorderLayout;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import logger.Logger;
import logger.LoggerFactory;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import scenarios.manager.Cloud.Node;
import statistics.distribution.Distribution;
import statistics.distribution.DistributionRepository;
import cloud.api.CloudAPI;
import cloud.api.CloudSnapshot;
import cloud.requestengine.RequestGenerator;
import cloud.requestengine.ResponseTimeService;

import common.AbstractGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-25
 *
 */

public class CloudGUI extends AbstractGUI {

	private static CloudGUI instance = new CloudGUI();
	private Logger logger = LoggerFactory.getLogger(CloudGUI.class, this);
	
	public static CloudGUI getInstance() {
		return instance;
	}
	
	private static final long serialVersionUID = -5915547650186857743L;
	private DistributionRepository distributionRepository = DistributionRepository.getInstance();
	private ResponseTimeService responseTimeService = ResponseTimeService.getInstance();
	private Distribution currentDistribution;
	public static final String INSTANCES_ITEM_DEFAULT = "Please select an instance";
	private String[] CPU_SPEEDS = new String[]{"1.2", "1.8", "2.0", "2.4", "2.8", "3.2", "3.8", "4.2"};
	private String[] BANDWIDTHES = new String[]{"1", "2", "3", "4", "5"};
	private String[] MEMORIES = new String[]{"1", "2", "4", "8", "16", "32"};
	private JPanel instanceControlPanel;
	private JTable instances;
	private JButton addInstanceButton;
	private JButton removeInstanceButton;
	private CloudAPI api;
	private RequestGenerator reqGen;
	private AddInstanceActionListener addInstanceActionListener = new AddInstanceActionListener(this);
	private RemoveInstanceActionListener removeInstanceActionListener = new RemoveInstanceActionListener(this);
	private DistributionSelectionActionListener distributionSelectionActionListener = new DistributionSelectionActionListener(this);
	private StartDistributionRequestActionListener startDistributionRequestActionListener = new StartDistributionRequestActionListener(this);
	private StopDistributionRequestActionListener stopDistributionRequestActionListener = new StopDistributionRequestActionListener(this);
	private InstanceTablePopupMenuListener instanceTablePopupMenuListener = new InstanceTablePopupMenuListener(this); 
	private JComboBox bandwidthes;
	private JTextArea sDownloads;
	private JComboBox cpuSpeed;
	private DefaultTableModel model;
	private JComboBox memories;
	private JPanel requestEnginePanel;
	private JComboBox distributions;
	private JTextField parameter1;
	private JTextField parameter2;
	private JTextField parameter3;
	private JLabel parameter1Lbl;
	private JLabel parameter2Lbl;
	private JLabel parameter3Lbl;
	private JPanel parametersPanel;
	private ChartPanel diagramPanel;
	private ChartPanel responseTimeDiagramPanel;
	private JPanel distributionPanelSelector;
	private JButton startDistributionRequestButton;
	private JLabel disLbl;
	private JPanel statisticsPanel;
	private JPanel snapshotPanel;
	private DefaultTableModel snapshotModel;
	private JTable snapshotTable;
	private String[] snapshotTableColumns = new String[]{"Snapshot ID", "Date"};
	private SnapshotPopupListener snapshotPopupListener = new SnapshotPopupListener(this);
	private List<CloudSnapshot> snapshots = new ArrayList<CloudSnapshot>();
	
	public CloudGUI() {
		createTabs();
		addWindowListener();
		this.setSize(400,600);
		registerListeners();
		setupLocation();
		setVisible(true);
	}

	private void setupLocation() {
		this.setLocation(400, 0);		
	}

	private void registerListeners() {
		addInstanceButton.addActionListener(addInstanceActionListener);
		removeInstanceButton.addActionListener(removeInstanceActionListener);		
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
		createMenuBar();
		createInstanceControlPanel();
		createRequestEngineTab();
		createStatisticsTab();
		createLogPanel();
		setLayout(new GridLayout(1, 1));
		add(tabbedPane);
	}

	private void createStatisticsTab() {
		statisticsPanel = new JPanel();
		statisticsPanel.setLayout(new GridLayout(2, 1));
		tabbedPane.addTab("Statistics", statisticsPanel);
		
		createSnapshotPanel();
		
	}

	private void createSnapshotPanel() {
		snapshotPanel = new JPanel();
		snapshotPanel.setLayout(new BorderLayout());
		snapshotPanel.setBorder(BorderFactory.createTitledBorder("Snapshot(s)"));
		
		createSnapshotTable();
		
		statisticsPanel.add(snapshotPanel);
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
		snapshotPanel.add(pane);
		
	}

	private void createResponseTimePanel(JFreeChart chart) {
		if (null != chart) {
			if (responseTimeDiagramPanel != null)
				statisticsPanel.remove(responseTimeDiagramPanel);
			responseTimeDiagramPanel = new ChartPanel(chart);
			responseTimeDiagramPanel.setBorder(BorderFactory.createTitledBorder("Response Time"));
			statisticsPanel.add(responseTimeDiagramPanel);
			statisticsPanel.revalidate();
		} else {
			logger.error("chart for responseTime can not be null");
		}
	}

	private void createRequestEngineTab() {
		requestEnginePanel = new JPanel();
		requestEnginePanel.setLayout(new GridLayout(2, 1));
		createDistributionPanel();
		tabbedPane.addTab("Request Engine", requestEnginePanel);
	}

	private void createDistributionPanel() {
		JPanel distributionPanel = new JPanel();
		distributionPanel.setBorder(BorderFactory.createTitledBorder("Distribution"));
		distributionPanel.setLayout(new GridLayout(3, 1));

// ------------------- //		
		distributionPanelSelector = new JPanel();
		distributionPanelSelector.setBorder(BorderFactory.createEmptyBorder());
		distributionPanelSelector.setLayout(new FlowLayout());
		
		disLbl = new JLabel("Distribution: ");
		distributionPanelSelector.add(disLbl);
		
		distributions = new JComboBox(distributionRepository.getDistributions());
		distributionPanelSelector.add(distributions);
		distributions.addActionListener(distributionSelectionActionListener);
// ------------------- //
		parametersPanel = new JPanel();
		parametersPanel.setBorder(BorderFactory.createEmptyBorder());
		parametersPanel.setLayout(new FlowLayout());
		
		parameter1 = new IntegerTextField(7);
		parameter2 = new IntegerTextField(7); 
		parameter3 = new IntegerTextField(7);
		
		parameter1Lbl = new JLabel("P1: ");
		parameter2Lbl = new JLabel("P2: ");
		parameter3Lbl = new JLabel("P3: ");
		
// ------------------- //		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder());
		buttonPanel.setLayout(new FlowLayout());
		
		startDistributionRequestButton = new JButton("Start");
		startDistributionRequestButton.addActionListener(startDistributionRequestActionListener);
		JButton stopDistributionRequest = new JButton("Stop");
		stopDistributionRequest.addActionListener(stopDistributionRequestActionListener);
		
		buttonPanel.add(startDistributionRequestButton);
		buttonPanel.add(stopDistributionRequest);
		
		distributionPanel.add(distributionPanelSelector);
		distributionPanel.add(parametersPanel);
		distributionPanel.add(buttonPanel);
		requestEnginePanel.add(distributionPanel);	
		decorateForUniformDistribution();
	}

	private void createDiagramPanel(JFreeChart chart) {
		if (null != chart) {
			if (diagramPanel != null)
				requestEnginePanel.remove(diagramPanel);
			diagramPanel = new ChartPanel(chart);
			diagramPanel.setBorder(BorderFactory.createTitledBorder("Diagram"));
			requestEnginePanel.add(diagramPanel);
			requestEnginePanel.revalidate();
		} else {
			logger.error("chart can not be null");
		}
	}

	private void createInstanceControlPanel() {
		instanceControlPanel = new JPanel(); 
		instanceControlPanel.setLayout(new GridLayout(2, 1));
		
		JPanel addInstancePanel = createAddInstancePanel();
		JPanel currentInstancesPanel = createCurrentInstancesPanel();
		
		instanceControlPanel.add(addInstancePanel);
		instanceControlPanel.add(currentInstancesPanel);
		tabbedPane.addTab("Instances", instanceControlPanel);
	}

	private JPanel createCurrentInstancesPanel() {
		JPanel currentInstancesPanel = new JPanel();
		currentInstancesPanel.setBorder(BorderFactory.createTitledBorder("Current Instances"));
		currentInstancesPanel.setLayout(new GridLayout(2,0));
		
		createInstanceTable(currentInstancesPanel);
		
		JPanel removeButtonPanel = new JPanel();
		removeButtonPanel.setBorder(BorderFactory.createEmptyBorder());
		removeButtonPanel.setLayout(new FlowLayout());
		
		removeInstanceButton = new JButton("Remove instance");
		removeButtonPanel.add(removeInstanceButton);
		currentInstancesPanel.add(removeButtonPanel);
		return currentInstancesPanel;
	}

	private void createInstanceTable(JPanel currentInstancesPanel) {
	    model = new DefaultTableModel(new String[][]{}, new String[]{"Name", "Address", "Status"});
		instances = new JTable(model){
			private static final long serialVersionUID = 8374219580041789497L;
			public boolean isCellEditable(int rowIndex, int colIndex) {
		          return false;
		        }
		};
		instances.addMouseListener(instanceTablePopupMenuListener);
		JScrollPane tableScrollPane = new JScrollPane(instances);
		currentInstancesPanel.add(tableScrollPane);
	}

	private JPanel createAddInstancePanel() {
		JPanel addInstancePanel = new JPanel();
		addInstancePanel.setBorder(BorderFactory.createTitledBorder("Launch Instance"));
		addInstancePanel.setLayout(new GridLayout(3, 1));
//--------------------------//		
		JPanel labelPanel = new JPanel();
		labelPanel.setBorder(BorderFactory.createEmptyBorder());
		labelPanel.setLayout(new BorderLayout());
		JLabel addANewInstancelbl = new JLabel("Add a new instance with the following configuration:");
		labelPanel.add(addANewInstancelbl);
		addInstancePanel.add(labelPanel);
//--------------------------//		
		JPanel nodeConfigurationsPanel = new JPanel();
		nodeConfigurationsPanel.setLayout(new GridLayout(4, 2));
		nodeConfigurationsPanel.setBorder(BorderFactory.createEmptyBorder());
		
		JLabel cpuSpeedlbl = new JLabel("CPU Speed (GHz)");
		nodeConfigurationsPanel.add(cpuSpeedlbl);
		
		cpuSpeed = new JComboBox(CPU_SPEEDS);
		cpuSpeed.setSelectedItem("2.0");
		nodeConfigurationsPanel.add(cpuSpeed);
		
		JLabel bandwidthlbl = new JLabel("Bandwidth (MB/s):");
		nodeConfigurationsPanel.add(bandwidthlbl);
		
		bandwidthes = new JComboBox(BANDWIDTHES);
		bandwidthes.setSelectedItem("2");
		nodeConfigurationsPanel.add(bandwidthes);
		
		JLabel memoryLbl = new JLabel("Memory (GB)");
		nodeConfigurationsPanel.add(memoryLbl);
		
		memories = new JComboBox(MEMORIES);
		memories.setSelectedIndex(2);
		nodeConfigurationsPanel.add(memories);
		
		JLabel simultaneousDownloadsLbl = new JLabel("Simulatenous Downloads:");
		nodeConfigurationsPanel.add(simultaneousDownloadsLbl);

		sDownloads = new JTextArea("200");
		nodeConfigurationsPanel.add(sDownloads);

		addInstancePanel.add(nodeConfigurationsPanel);
//--------------------------//		
		JPanel addInstanceButtonPanel = new JPanel();
		addInstanceButtonPanel.setBorder(BorderFactory.createEmptyBorder());
		addInstanceButtonPanel.setLayout(new GridLayout(2,2));
		
		addInstanceButton = new JButton("Launch Instance");
		addInstanceButtonPanel.add(addInstanceButton);
		
		addInstancePanel.add(addInstanceButtonPanel);
		return addInstancePanel;
	}
	
	public synchronized void addNewInstance(Node instance) {
		if (null != instance) {
			model.insertRow(instances.getRowCount(), new String[]{instance.getNodeName(), instance.getIP()+":"+instance.getPort(), "Healthy"});
		} else {
			logger.error("instance can not be null");
		}
	}
	
	public void killSelectedInstances() {
		if (api != null) {
			if (instances.getSelectedRows() == null) return;
			int[] rows = instances.getSelectedRows();
			for (int i=instances.getSelectedRows().length-1; i>=0; i--) {
				String nodeName = (String) model.getValueAt(rows[i], 0);
				String address = (String) model.getValueAt(rows[i], 1);
				model.removeRow(rows[i]);
				api.kill(Node.fromString(nodeName + "@" + address));
			}
		} else {
			log("ERROR: CloudAPI component is not started or is dead");
		}
	}
	
	public void suspectInstance(Node node) {
		if ( null != node) {
			for (int i=0; i<model.getRowCount(); i++) {
				if ( ((String)(model.getValueAt(i, 0))).equals(node.getNodeName()) && 
						((String)(model.getValueAt(i, 1))).equals(node.getIP()+":"+node.getPort())) {
					model.setValueAt("Unhealthy", i, 2);
					return;
				}
			}
		} else {
			logger.error("node can not be null");
		}
	}

	public void restoreInstance(Node node) {
		if ( null != node ) {
			for (int i=0; i<model.getRowCount(); i++) {
				if ( ((String)(model.getValueAt(i, 0))).equals(node.getNodeName()) && 
						((String)(model.getValueAt(i, 1))).equals(node.getIP()+":"+node.getPort())) {
					model.setValueAt("Healthy", i, 2);
					return;
				}
			}
		} else {
			logger.error("node can not be null");
		}
	}
	
	public void decorateExponentialDistribution() {
		parametersPanel.removeAll();
		parameter1Lbl.setText("Minimum Interval (s): ");
		parametersPanel.add(parameter1Lbl);
		parameter1.setText("10");
		parametersPanel.add(parameter1);
		currentDistribution = distributionRepository.getExponentialDistribution();
		createDiagramPanel(currentDistribution.getChart());
	}

	public void decorateForUniformDistribution() {
		parametersPanel.removeAll();
		parameter1Lbl.setText("Start (s): ");
		parametersPanel.add(parameter1Lbl);
		parameter1.setText("1");
		parametersPanel.add(parameter1);
		
		parameter2Lbl.setText("End (s): ");
		parametersPanel.add(parameter2Lbl);
		parameter2.setText("10");
		parametersPanel.add(parameter2);
		
		currentDistribution = distributionRepository.getUniformDistribution();
		createDiagramPanel(currentDistribution.getChart());
	}
	
	public void decorateForConstantDistribution() {
		parametersPanel.removeAll();
		parameter1Lbl.setText("Value (s): ");
		parametersPanel.add(parameter1Lbl);
		parameter1.setText("5");
		parametersPanel.add(parameter1);
		
		currentDistribution = distributionRepository.getConstantDistribution();
		createDiagramPanel(currentDistribution.getChart());
	}


	public void decorateForCustomDistribution(List<String> lines) {
		parametersPanel.removeAll();
		currentDistribution = distributionRepository.getCustomDistribution(lines);
		
		createDiagramPanel(currentDistribution.getChart());
	}
	
	public void decorateForInValidDistribution() {
		parametersPanel.removeAll();
		parameter1Lbl.setText("Invalid Distribution file, choose a correct distribution!");
		parametersPanel.add(parameter1Lbl);
	}

	public synchronized void instanceAdded() {
		addInstanceButton.setEnabled(true);
	}
	
	public synchronized void instanceRemoved() {
		removeInstanceButton.setEnabled(true);
	}
	
	public void disableAddInstanceButton() {
		addInstanceButton.setEnabled(false);
	}
	
	public void disableRemoveInstanceButton() {
		removeInstanceButton.setEnabled(false);
	}
	
	public void setCloudProviderAPI(CloudAPI api) {
		this.api = api;		
	}
	
	public void setRequestGenerator(RequestGenerator reqGen) {
		this.reqGen = reqGen;
	}
	
	public CloudAPI getCloudAPI() {
		return api;
	}
	
	public JTable getInstances() {
		return instances;
	}

	public JButton getAddInstance() {
		return addInstanceButton;
	}

	public JButton getRemoveInstanceButton() {
		return removeInstanceButton;
	}

	public JComboBox getBandwidthes() {
		return bandwidthes;
	}

	public JTextArea getsDownloads() {
		return sDownloads;
	}

	public JComboBox getCpuSpeed() {
		return cpuSpeed;
	}

	public JComboBox getMemories() {
		return memories;
	}

	public JComboBox getDistributions() {
		return distributions;
	}

	public void startDistributionBehaviour() {
		distributionPanelSelector.setEnabled(false);
		distributions.setEnabled(false);
		parameter1.setEnabled(false);
		parameter1Lbl.setEnabled(false);
		parameter2.setEnabled(false);
		parameter2Lbl.setEnabled(false);
		parameter3.setEnabled(false);
		parameter3Lbl.setEnabled(false);
		disLbl.setEnabled(false);
		startDistributionRequestButton.setEnabled(false);
		parametersPanel.setEnabled(false);		
		
		currentDistribution.setParameters(parameter1.getText(), parameter2.getText(), parameter3.getText());
		reqGen.updateDistribution(currentDistribution);		
	}
	
	public void stopDistributionBehaviour() {
		distributionPanelSelector.setEnabled(true);
		distributions.setEnabled(true);
		parameter1.setEnabled(true);
		parameter1Lbl.setEnabled(true);
		parameter2.setEnabled(true);
		parameter2Lbl.setEnabled(true);
		parameter3.setEnabled(true);
		parameter3Lbl.setEnabled(true);
		disLbl.setEnabled(true);
		startDistributionRequestButton.setEnabled(true);
		parametersPanel.setEnabled(true);		
		
		reqGen.stopCurrentDistribution();
	}

	public void updateResponseTime() {
		createResponseTimePanel(responseTimeService.getChart());
	}

	public void takeSnapshot() {
		api.takeSnapshot();
	}

	public void saveSelectedSnapshotTo(File selectedDir) {
		int id = (Integer) snapshotModel.getValueAt(snapshotTable.getSelectedRow(), 0);
		CloudSnapshot snapshot = getSnapshotWithId(id);
		saveSnapshotTo(selectedDir, snapshot);		
	}

	public void saveAllSnapshotsTo(File selectedDir) {
		for (CloudSnapshot snapshot : snapshots) {
			saveSnapshotTo(selectedDir, snapshot);
		}		
	}

	private void saveSnapshotTo(File selectedFile, CloudSnapshot snapshot) {
		File nodeDir = new File(selectedFile.getPath() + File.separatorChar + this.getTitle());
		if (!nodeDir.exists()) nodeDir.mkdir();
		File snapshotDir = new File(nodeDir.getPath() + File.separatorChar + snapshot.getId());
		if (!snapshotDir.exists()) {
			snapshotDir.mkdir();
		
			writeResponseTimeDiagram(snapshot, snapshotDir);
			saveLogTo(snapshotDir, snapshot.getLog());
		}
	}
	
	private void writeResponseTimeDiagram(CloudSnapshot snapshot, File snapshotDir) {
		if (snapshot.getChart() != null) {
			BufferedImage cpuImage = snapshot.getChart().createBufferedImage(833, 500);
			File rtFile = new File(snapshotDir.getPath() + File.separatorChar + "ResponseTimeDiagram.png");
			try {
				ImageIO.write(cpuImage, "PNG", rtFile);
			} catch (IOException e) {
				log(e.getMessage());
			}
		}		
	}

	public void deleteAllSnapshots() {
		snapshots.clear();
		for (int i=snapshotModel.getRowCount()-1; i>=0; i--)
			snapshotModel.removeRow(i);		
	}

	public JTable getSnapshotTable() {
		return snapshotTable;
	}

	public void addSnapshot(CloudSnapshot cloudSnapshot) {
		if (null != cloudSnapshot) {
			snapshotModel.insertRow(snapshotModel.getRowCount(), new Object[]{cloudSnapshot.getId(), cloudSnapshot.getDate()});
			cloudSnapshot.addLogText(logTextArea.getText());
			snapshots.add(cloudSnapshot);
		} else {
			logger.error("cloudSnapshot can not be null");
		}
	}
	
	private CloudSnapshot getSnapshotWithId(int id) {
		for (CloudSnapshot snapshot : snapshots) {
			if (snapshot.getId() == id)
				return snapshot;
		}
		return null;
	}

	public void restartSelectedInstance() {
		String address = (String) model.getValueAt(instances.getSelectedRow(), 1);
		api.restartInstance(address);
	}

	@Override
	public void createFileMenuItems() {
		// TODO Auto-generated method stub
		
	}


}
