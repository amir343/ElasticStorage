package econtroller.gui;

import cloud.gui.IntegerTextField;
import common.AbstractGUI;
import econtroller.controller.Controller;
import econtroller.modeler.Modeler;
import econtroller.modeler.ModelerSnapshot;
import instance.gui.SnapshotTablePopupListener;
import logger.Logger;
import logger.LoggerFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ControllerGUI extends AbstractGUI {

	private Logger logger = LoggerFactory.getLogger(ControllerGUI.class, this);
    public static ControllerGUI getInstance() {
        return instance;
    }

    private static final long serialVersionUID = 8924494948307221974L;

    private static final int SENSE_MIN = 5;
    private static final int SENSE_MAX = 120;
    private static final int SENSE_INIT = 25;
    private static final int ACT_MIN = 10;
    private static final int ACT_MAX = 180;
    private static final int ACT_INIT = 80;
    private static ControllerGUI instance = new ControllerGUI();
    private JLabel actValueLabel;
    private JLabel senseValueLabel;
	private JPanel controlPanel;
	private JPanel cloudProviderSection;
	private JTextField ipTxt;
	private JTextField portTxt;
	private JButton connectBtn;
	private JButton disconnectBtn;
	private JPanel cloudProviderConnectionPanel;
	private JPanel connectionDescriptionPanel;
	private ConnectActionListener connectActionListener = new ConnectActionListener(this);
	private DisconnectActionListener disconnectActionListener = new DisconnectActionListener(this);
	private StartControllerActionListener startControllerActionListener = new StartControllerActionListener(this);
	private StopControllerActionListener stopControllerActionListener = new StopControllerActionListener(this);
	private Controller controller;
	private JLabel portLbl;
	private JLabel ipLbl;
	private JPanel controllerDesignPanel;
	private JPanel controllerDesignSection;
	private JComboBox controllers;
	private JPanel controllerDesignButtonsPanel;
	private JButton startControllerBtn;
	private JButton stopControllerBtn;
	private JSlider senseSlider;
	private JSlider actSlider;
	private JLabel actLabel;
	private JLabel senseLabel;
	private JPanel modelerPanel;
	private JPanel modelerControlPanelSection;
	private JPanel maxInstanceSection;
	private JPanel modelerButtonsSection;
	private JButton startModelerButton;
	private JButton stopModelerButton;
	private JLabel maxNrInstancesLbl;
	private IntegerTextField maxInstanceText;
	private JPanel cpuLoadPanel;
	private JPanel cpuSTDPanel;
	private JPanel responseTimePanel;
	private JPanel costPanel;
	private JPanel nrInstancePanel;
	private Modeler modeler;
	private ChartPanel cpuChartPanel;
	private ChartPanel cpuSTDChartPanel;
	private ChartPanel rtChartPanel;
	private ChartPanel costChartPanel;
	private ChartPanel nrInstanceChartPanel;
	private JButton dumpDataIntoFilesBtn;
	private JButton resetModelerButton;
	private ChartPanel throughputChartPanel;
	private JPanel throughputPanel;
	private JPanel snapshotPanel;
	private DefaultTableModel snapshotModel;
	private JTable snapshotTable;
	private String[] snapshotTableColumns = new String[]{"Snapshot ID", "Date"};
	private SnapshotTablePopupListener snapshotPopupListener = new SnapshotTablePopupListener(this);
	private List<ModelerSnapshot> snapshots = new ArrayList<ModelerSnapshot>();
	private IntegerTextField minInstanceText;
	private Container instanceLayout;
	private JLabel samplingOrderLbl;
	private IntegerTextField samplingText;
	private IntegerTextField orderingText;
	private Container sampleOrderLayout;
    private JCheckBox orderedEnabled;

	
	public ControllerGUI() {
        setUIManager();
		createMenuBar();
		createTabs();
		addWindowListener();
		this.setSize(400,600);
		registerListeners();
		setupLocation();
		setVisible(true);
	}
	
	private void setupLocation() {
	}

	private void registerListeners() {
		connectBtn.addActionListener(connectActionListener);
		disconnectBtn.addActionListener(disconnectActionListener);
		startControllerBtn.addActionListener(startControllerActionListener);
		stopControllerBtn.addActionListener(stopControllerActionListener);
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
		createControlPanel();
		createModelerPanel();
		createSnapshotPanel();
		createLogPanel();
		setLayout(new GridLayout(1, 1));
		add(tabbedPane);
	}

	private void createSnapshotPanel() {
		snapshotPanel = new JPanel();
		snapshotPanel.setLayout(new GridLayout(1,1));
		
		createSnapshotTable();
		
		tabbedPane.addTab("Snapshot", snapshotPanel);
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

	private void createModelerPanel() {
		modelerPanel = new JPanel();
		modelerPanel.setLayout(new GridLayout(1,1));
		
		createModelerControlPanel();
		
		tabbedPane.addTab("System Identification", modelerPanel);
		tabbedPane.setEnabledAt(1, false);
	}

	private void createModelerControlPanel() {
		modelerControlPanelSection = new JPanel();
		GroupLayout group = new GroupLayout(modelerControlPanelSection);
		modelerControlPanelSection.setLayout(group);
		group.setAutoCreateGaps(true);
		group.setAutoCreateContainerGaps(true);
		
		modelerControlPanelSection.setBorder(BorderFactory.createTitledBorder("Black Box System Identification"));

		createSamplingAndNodeOrderingSection();
		createNrInstanceSection();
		createControlPanelButtons();
		createChartPanels();
		
		group.setHorizontalGroup(
				group.createSequentialGroup()
				.addGroup(group.
						createParallelGroup()
							.addComponent(orderedEnabled)
							.addComponent(samplingOrderLbl)
							.addComponent(maxNrInstancesLbl)
							.addComponent(startModelerButton)
							.addComponent(dumpDataIntoFilesBtn)
							.addComponent(cpuLoadPanel)
							.addComponent(responseTimePanel)
							.addComponent(nrInstancePanel))
				.addGroup(group.
						createParallelGroup()
							.addComponent(sampleOrderLayout)
							.addComponent(instanceLayout)
							.addComponent(stopModelerButton)
							.addComponent(resetModelerButton)
							.addComponent(cpuSTDPanel)
							.addComponent(costPanel)
							.addComponent(throughputPanel))
		);
		
		group.setVerticalGroup(
				group.createSequentialGroup()
				.addGroup(group.
						createParallelGroup()
						.addComponent(orderedEnabled)
						 )
				.addGroup(group.
						createParallelGroup()
						.addComponent(samplingOrderLbl)
						.addComponent(sampleOrderLayout)
						 )
				.addGroup(group.
						createParallelGroup()
							.addComponent(maxNrInstancesLbl)
							.addComponent(instanceLayout)
						 )
				.addGroup(group.
						createParallelGroup()
							.addComponent(startModelerButton)
							.addComponent(stopModelerButton)
						 )
				.addGroup(group.
						createParallelGroup()
							.addComponent(dumpDataIntoFilesBtn)
							.addComponent(resetModelerButton)
						 )
				.addGroup(group.
						createParallelGroup()
							.addComponent(cpuLoadPanel)
							.addComponent(cpuSTDPanel)
						 )
				.addGroup(group.
						createParallelGroup()
							.addComponent(responseTimePanel)
							.addComponent(costPanel)
						 )
				.addGroup(group.
						createParallelGroup()
							.addComponent(nrInstancePanel)
							.addComponent(throughputPanel)
						 )
		);
		
		modelerPanel.add(modelerControlPanelSection);
	}

	private void createChartPanels() {
		cpuLoadPanel = new JPanel();
		cpuLoadPanel.setLayout(new GridLayout(1,1));
		cpuLoadPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		cpuSTDPanel = new JPanel();
		cpuSTDPanel.setLayout(new GridLayout(1,1));
		cpuSTDPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		responseTimePanel = new JPanel();
		responseTimePanel.setLayout(new GridLayout(1,1));
		responseTimePanel.setBorder(BorderFactory.createTitledBorder(""));
		
		costPanel = new JPanel();
		costPanel.setLayout(new GridLayout(1,1));
		costPanel.setBorder(BorderFactory.createTitledBorder(""));
		
		nrInstancePanel = new JPanel();
		nrInstancePanel.setLayout(new GridLayout(1,1));
		nrInstancePanel.setBorder(BorderFactory.createTitledBorder(""));
		
		throughputPanel = new JPanel();
		throughputPanel.setLayout(new GridLayout(1,1));
		throughputPanel.setBorder(BorderFactory.createTitledBorder(""));
		
	}

	private void createControlPanelButtons() {
		startModelerButton = new JButton("Start");
		startModelerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startModelerButton.setEnabled(false);
				disableSIConfigs();
				modeler.startModeler(orderedEnabled.isSelected(),
                                     Integer.parseInt(maxInstanceText.getText()),
									 Integer.parseInt(minInstanceText.getText()),
									 Integer.parseInt(samplingText.getText()),
									 Integer.parseInt(orderingText.getText())
				);
			}
		});
		
		stopModelerButton = new JButton("Stop");
		stopModelerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startModelerButton.setEnabled(true);
				stopModelerButton.setEnabled(false);
				modeler.stopModeler();
				enableSIConfigs();
			}
		});
		
		dumpDataIntoFilesBtn = new JButton("Dump data into files");
		dumpDataIntoFilesBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String result = modeler.estimateParameters();
			}
		});
		
		resetModelerButton = new JButton("Reset");
		resetModelerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modeler.reset();
			}
		});
		
		
	}

	private void createNrInstanceSection() {
		maxNrInstancesLbl = new JLabel("# of Instances");
		minInstanceText = new IntegerTextField("2");
		maxInstanceText = new IntegerTextField("8");
		instanceLayout = new Container();
		instanceLayout.setLayout(new GridLayout(1, 2));
		instanceLayout.add(minInstanceText);
		instanceLayout.add(maxInstanceText);
	}
	
	private void createSamplingAndNodeOrderingSection() {
        orderedEnabled = new JCheckBox("Ordering enabled");
        orderedEnabled.setSelected(true);
		samplingOrderLbl = new JLabel("Sampling & Ordering (s)");
		samplingText = new IntegerTextField("10");
		orderingText = new IntegerTextField("90");
		sampleOrderLayout = new Container();
		sampleOrderLayout.setLayout(new GridLayout(1, 2));
		sampleOrderLayout.add(samplingText);
		sampleOrderLayout.add(orderingText);
	}
	
	private void disableSIConfigs() {
        orderedEnabled.setEnabled(false);
		samplingText.setEnabled(false);
		orderingText.setEnabled(false);
		minInstanceText.setEnabled(false);
		maxInstanceText.setEnabled(false);
	}

	private void enableSIConfigs() {
        orderedEnabled.setEnabled(true);
		samplingText.setEnabled(true);
		orderingText.setEnabled(true);
		minInstanceText.setEnabled(true);
		maxInstanceText.setEnabled(true);
	}
	
	private void createControlPanel() {
		controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 1));
		
		createCloudProviderConnectionSection();
		createControllerDesignSection();
		
		tabbedPane.addTab("Control Panel", controlPanel);
		
	}

	private void createControllerDesignSection() {
		controllerDesignPanel = new JPanel();
		controllerDesignPanel.setLayout(new BoxLayout(controllerDesignPanel, BoxLayout.Y_AXIS));
		controllerDesignPanel.setBorder(BorderFactory.createTitledBorder("Controller Configurations"));
		
		createDesignSection();
		createSenseActTimersSection();
		createDesignSectionButtons();
		
		controlPanel.add(controllerDesignPanel);		
	}

	private void createSenseActTimersSection() {
		createSenseSlider();
		createActSlider();
	}

	private void createSenseSlider() {
        Box hbox = Box.createHorizontalBox();
        senseLabel = new JLabel("Sense every ", JLabel.CENTER);
        senseValueLabel = new JLabel(String.valueOf(SENSE_INIT), JLabel.CENTER);
        JLabel senseSecondLabel = new JLabel(" (s)", JLabel.CENTER);
		
		senseSlider = new JSlider(JSlider.HORIZONTAL, SENSE_MIN, SENSE_MAX, SENSE_INIT);
		senseSlider.setMajorTickSpacing(20);
		senseSlider.setMinorTickSpacing(5);
		senseSlider.setPaintLabels(true);
		senseSlider.setPaintTicks(true);
		senseSlider.setPaintTrack(true);

        senseSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JSlider source = (JSlider) changeEvent.getSource();
                int value = source.getValue();
                senseValueLabel.setText(String.valueOf(value));
            }
        });

        hbox.add(senseLabel); hbox.add(senseValueLabel); hbox.add(senseSecondLabel);
		controllerDesignPanel.add(hbox);
		controllerDesignPanel.add(senseSlider);
	}

	private void createActSlider() {
        Box hbox = Box.createHorizontalBox();
		actLabel = new JLabel("Act every ", JLabel.CENTER);
		actValueLabel = new JLabel(String.valueOf(ACT_INIT), JLabel.CENTER);
        JLabel actSecondLabel = new JLabel(" (s)", JLabel.CENTER);

		actSlider = new JSlider();
		actSlider = new JSlider(JSlider.HORIZONTAL, ACT_MIN, ACT_MAX, ACT_INIT);
		actSlider.setMajorTickSpacing(20);
		actSlider.setMinorTickSpacing(5);
		actSlider.setPaintLabels(true);
		actSlider.setPaintTicks(true);
		actSlider.setPaintTrack(true);
        actSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                JSlider source = (JSlider) changeEvent.getSource();
                int value = source.getValue();
                actValueLabel.setText(String.valueOf(value));
            }
        });
        hbox.add(actLabel); hbox.add(actValueLabel); hbox.add(actSecondLabel);
		controllerDesignPanel.add(hbox);
		controllerDesignPanel.add(actSlider);
		
	}

	private void createDesignSectionButtons() {
		controllerDesignButtonsPanel = new JPanel();
		controllerDesignButtonsPanel.setLayout(new FlowLayout());
		controllerDesignButtonsPanel.setBorder(BorderFactory.createEmptyBorder());
		
		startControllerBtn = new JButton("Start");
		controllerDesignButtonsPanel.add(startControllerBtn);
		
		stopControllerBtn = new JButton("Stop");
		controllerDesignButtonsPanel.add(stopControllerBtn);
		stopControllerBtn.setEnabled(false);
		
		controllerDesignPanel.add(controllerDesignButtonsPanel);		
	}

	private void createDesignSection() {
		controllerDesignSection = new JPanel();
		controllerDesignSection.setLayout(new FlowLayout());
		controllerDesignSection.setBorder(BorderFactory.createEmptyBorder());
		
		controllers = new JComboBox();
		
		controllerDesignSection.add(controllers);
		controllerDesignPanel.add(controllerDesignSection);
	}

	private void createCloudProviderConnectionSection() {
		cloudProviderSection = new JPanel();
		cloudProviderSection.setLayout(new GridLayout(3, 0));
		cloudProviderSection.setBorder(BorderFactory.createTitledBorder("Cloud Provider Connection"));
		
		createDescriptionPanel();
		createConnectionPanel();
		
		controlPanel.add(cloudProviderSection);
	}

	private void createDescriptionPanel() {
		connectionDescriptionPanel = new JPanel();
		connectionDescriptionPanel.setLayout(new FlowLayout());
		connectionDescriptionPanel.setBorder(BorderFactory.createEmptyBorder());
		
		JLabel descriptionLbl = new JLabel("Enter the address of cloud provider to connect");
		
		connectionDescriptionPanel.add(descriptionLbl);
		
		cloudProviderSection.add(connectionDescriptionPanel);
	}

	private void createConnectionPanel() {
		cloudProviderConnectionPanel = new JPanel();
		cloudProviderConnectionPanel.setLayout(new GridLayout(3,2));
		cloudProviderConnectionPanel.setBorder(BorderFactory.createEmptyBorder());
		
		ipLbl = new JLabel("IP: ");
		cloudProviderConnectionPanel.add(ipLbl);
		
		ipTxt = new IPTextField("127.0.0.1");
		cloudProviderConnectionPanel.add(ipTxt);
		
		portLbl = new JLabel("Port: ");
		cloudProviderConnectionPanel.add(portLbl);
		
		portTxt = new PortTextField("23444");
		cloudProviderConnectionPanel.add(portTxt);
		
		connectBtn = new JButton("Connect");
		cloudProviderConnectionPanel.add(connectBtn);

		disconnectBtn = new JButton("Disconnect");
		disconnectBtn.setEnabled(false);
		cloudProviderConnectionPanel.add(disconnectBtn);
		
		cloudProviderSection.add(cloudProviderConnectionPanel);
	}

	public void disableConnectionSection() {
		ipLbl.setEnabled(false);
		ipTxt.setEnabled(false);
		portLbl.setEnabled(false);
		portTxt.setEnabled(false);
		connectBtn.setEnabled(false);
		disconnectBtn.setEnabled(true);
	}

	public void enableConnectionSection() {
		ipLbl.setEnabled(true);
		ipTxt.setEnabled(true);
		portLbl.setEnabled(true);
		portTxt.setEditable(true);
		connectBtn.setEnabled(true);
		disconnectBtn.setEnabled(false);
	}
	
	public void disableControllerDesignSection() {
		controllers.setEnabled(false);
		senseSlider.setEnabled(false);
		actSlider.setEnabled(false);
		senseLabel.setEnabled(false);
		actLabel.setEnabled(false);
		startControllerBtn.setEnabled(false);
		stopControllerBtn.setEnabled(true);
	}

	public void enableControllerDesignSection() {
		controllers.setEnabled(true);
		senseSlider.setEnabled(true);
		actSlider.setEnabled(true);
		senseLabel.setEnabled(true);
		actLabel.setEnabled(true);
		startControllerBtn.setEnabled(true);
		stopControllerBtn.setEnabled(false);
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	public void connectToCloudProvider() {
		controller.connectToCloudProvider(ipTxt.getText(), portTxt.getText());
	}

	public boolean validateConnectionParameter() {
		if (ipTxt.getText() == null && ipTxt.getText().equals(""))
			return false;
		if (portTxt.getText() == null && portTxt.getText().equals(""))
			return false;
		return true;
	}

	public void disconnectFromCloudProvider() {
		controller.disconnectFromCloudProvider();
		enableConnectionSection();
	}

	@Override
	public void takeSnapshot() {
		modeler.takeSnapshot();		
	}

	@Override
	public void createFileMenuItems() {
		// TODO Auto-generated method stub
		
	}

	public void addControllers(List<String> controllerNames) {
		for (String name : controllerNames) {
			controllers.addItem(name);
		}
		controllers.revalidate();
	}

	public void startController() {
		controller.startController((String)controllers.getSelectedItem(), senseSlider.getValue(), actSlider.getValue());		
	}

	public void stopController() {
		controller.stopController();
		
	}
	
	public static void main(String[] args) {
		ControllerGUI.getInstance();
		
	}

	public void setModeler(Modeler modeler) {
		this.modeler = modeler;
	}

	public void updateCpuSTDChart(JFreeChart cpuSTDChart) {
		if (cpuSTDChartPanel != null)
			cpuSTDPanel.remove(cpuSTDChartPanel);
		cpuSTDChartPanel = new ChartPanel(cpuSTDChart);
		cpuSTDPanel.add(cpuSTDChartPanel);
		cpuSTDPanel.revalidate();
	}

	public void updateCpuLoadChart(JFreeChart cpuLoadChart) {
		if (cpuChartPanel != null)
			cpuLoadPanel.remove(cpuChartPanel);
		cpuChartPanel = new ChartPanel(cpuLoadChart);
		cpuChartPanel.setSize(cpuLoadPanel.getWidth(), cpuLoadPanel.getHeight());
		cpuLoadPanel.add(cpuChartPanel);
		cpuLoadPanel.revalidate();
	}

	public void updateResponseTimeChart(JFreeChart responseTimeChart) {
		if (rtChartPanel != null)
			responseTimePanel.remove(rtChartPanel);
		rtChartPanel = new ChartPanel(responseTimeChart);
		responseTimePanel.add(rtChartPanel);
		responseTimePanel.revalidate();
	}

	public void updateTotalCostChart(JFreeChart totalCostChart) {
		if (costChartPanel != null)
			costPanel.remove(costChartPanel);
		costChartPanel = new ChartPanel(totalCostChart);
		costPanel.add(costChartPanel);
		costPanel.revalidate();		
	}

	public void updateNrOfInstancesChart(JFreeChart nrInstancesChart) {
		if (nrInstanceChartPanel != null)
			nrInstancePanel.remove(nrInstanceChartPanel);
		nrInstanceChartPanel = new ChartPanel(nrInstancesChart);
		nrInstancePanel.add(nrInstanceChartPanel);
		nrInstancePanel.revalidate();		
	}

	public void updateThroughputChart(JFreeChart averageThroughputChart) {
		if (throughputChartPanel != null)
			throughputPanel.remove(throughputChartPanel);
		throughputChartPanel = new ChartPanel(averageThroughputChart);
		throughputPanel.add(throughputChartPanel);
		throughputPanel.revalidate();		
	}

	@Override
	public void saveAllSnapshotsTo(File selectedDir) {
		for (ModelerSnapshot snapshot : snapshots) {
			saveSnapshotTo(selectedDir, snapshot);
		}		
	}

	@Override
	public void deleteAllSnapshots() {
		snapshots.clear();
		for (int i=snapshotModel.getRowCount()-1; i>=0; i--)
			snapshotModel.removeRow(i);		
	}

	@Override
	public JTable getSnapshotTable() {
		return snapshotTable;
	}

	public void addSnapshot(ModelerSnapshot snapshot) {
		if (null != snapshot) {
			snapshotModel.insertRow(snapshotModel.getRowCount(), new Object[]{snapshot.getId(), snapshot.getDate()});
			snapshot.addLogText(logTextArea.getText());
			snapshots.add(snapshot);
		} else {
			logger.error("snapshot can not be null");
		}
	}
	
	@Override
	public void saveSelectedSnapshotTo(File selectedDir) {
		int id = (Integer) snapshotModel.getValueAt(snapshotTable.getSelectedRow(), 0);
		ModelerSnapshot snapshot = getSnapshotWithId(id);
		saveSnapshotTo(selectedDir, snapshot);
	}
	
	private void saveSnapshotTo(File selectedFile, ModelerSnapshot snapshot) {
		File nodeDir = new File(selectedFile.getPath() + File.separatorChar + this.getTitle());
		if (!nodeDir.exists()) nodeDir.mkdir();
		File snapshotDir = new File(nodeDir.getPath() + File.separatorChar + snapshot.getId());
		if (!snapshotDir.exists()) {
			snapshotDir.mkdir();
		
			writePNG(snapshot.getAverageThroughputChart(), snapshotDir, "AverageThroughput.png");
			writePNG(snapshot.getBandwidthChart(), snapshotDir, "AverageBandwidth.png");
			writePNG(snapshot.getCpuChart(), snapshotDir, "AverageCPULoad.png");
			writePNG(snapshot.getNrInstancesChart(), snapshotDir, "NumberOfInstances.png");
			writePNG(snapshot.getResponseTimeChart(), snapshotDir, "AverageResponseTime.png");
			writePNG(snapshot.getTotalCostChart(), snapshotDir, "TotalCost.png");
			saveLogTo(snapshotDir, snapshot.getLogText());
		}
	}
	
	private ModelerSnapshot getSnapshotWithId(int id) {
		for (ModelerSnapshot snapshot : snapshots) {
			if (snapshot.getId() == id)
				return snapshot;
		}
		return null;
	}
	
	public void enableSystemIdentificationPanel() {
		tabbedPane.setEnabledAt(1, true);
		startModelerButton.setEnabled(true);
		stopModelerButton.setEnabled(true);
	}

	public void disableSystemIdentificationPanel() {
		tabbedPane.setEnabledAt(1, false);
		modeler.stopModeler();
		startModelerButton.setEnabled(true);
		stopModelerButton.setEnabled(false);
	}

    public void disableModeler() {
        orderedEnabled.setEnabled(false);
        samplingText.setEnabled(false);
        orderingText.setEnabled(false);
        minInstanceText.setEnabled(false);
        maxInstanceText.setEnabled(false);
        startModelerButton.setEnabled(false);
        stopModelerButton.setEnabled(false);
        dumpDataIntoFilesBtn.setEnabled(false);
        resetModelerButton.setEnabled(false);
    }

    public void enableModeler() {
        orderedEnabled.setEnabled(true);
        samplingText.setEnabled(true);
        orderingText.setEnabled(true);
        minInstanceText.setEnabled(true);
        maxInstanceText.setEnabled(true);
        startModelerButton.setEnabled(true);
        dumpDataIntoFilesBtn.setEnabled(true);
        resetModelerButton.setEnabled(true);
    }
	
}
