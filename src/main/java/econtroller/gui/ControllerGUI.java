package econtroller.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import common.AbstractGUI;

import econtroller.controller.Controller;
import econtroller.modeler.Modeler;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ControllerGUI extends AbstractGUI {

	private static final long serialVersionUID = 8924494948307221974L;
	private static final int SENSE_MIN = 5;
	private static final int SENSE_MAX = 120;
	private static final int SENSE_INIT = 5;
	private static final int ACT_MIN = 5;
	private static final int ACT_MAX = 120;
	private static final int ACT_INIT = 60;
	private static ControllerGUI instance = new ControllerGUI();
	
	public static ControllerGUI getInstance() {
		return instance;
	}

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
	private JTextField maxInstanceText;
	private JPanel cpuPanel;
	private JPanel bandwidthPanel;
	private JPanel responseTimePanel;
	private JPanel costPanel;
	private JPanel nrInstancePanel;
	private Modeler modeler;
	
	public ControllerGUI() {
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
		creatControlPanel();
		createModelerPanel();
		createLogPanel();
		setLayout(new GridLayout(1, 1));
		add(tabbedPane);
	}

	private void createModelerPanel() {
		modelerPanel = new JPanel();
		modelerPanel.setLayout(new FlowLayout());
		
		createModelerControlPanel();
		
		
		tabbedPane.addTab("Black Box System Identification", modelerPanel);
	}

	private void createModelerControlPanel() {
		modelerControlPanelSection = new JPanel();
		GroupLayout group = new GroupLayout(modelerControlPanelSection);
		modelerControlPanelSection.setLayout(group);
		modelerControlPanelSection.setBorder(BorderFactory.createTitledBorder("Control Panel"));

		createMaximumInstanceSection();
		createControlPanelButtons();
		createChartPanels();
		
		group.setHorizontalGroup(
				group.createSequentialGroup()
				.addGroup(group.
						createParallelGroup().addComponent(maxNrInstancesLbl).addComponent(startModelerButton).addComponent(cpuPanel).addComponent(responseTimePanel).addComponent(nrInstancePanel))
				.addGroup(group.
						createParallelGroup().addComponent(maxInstanceText).addComponent(stopModelerButton).addComponent(bandwidthPanel).addComponent(costPanel))
		);
		
		group.setVerticalGroup(
				group.createSequentialGroup()
				.addGroup(group.createParallelGroup().addComponent(maxNrInstancesLbl).addComponent(maxInstanceText))
				.addGroup(group.createParallelGroup().addComponent(startModelerButton).addComponent(stopModelerButton))
				.addGroup(group.createParallelGroup().addComponent(cpuPanel).addComponent(bandwidthPanel))
				.addGroup(group.createParallelGroup().addComponent(responseTimePanel).addComponent(costPanel))
				.addGroup(group.createSequentialGroup().addComponent(nrInstancePanel))
		);		
		
		modelerPanel.add(modelerControlPanelSection);
	}

	private void createChartPanels() {
		cpuPanel = new JPanel();
		cpuPanel.setBorder(BorderFactory.createTitledBorder("Average CPU"));
		
		bandwidthPanel = new JPanel();
		bandwidthPanel.setBorder(BorderFactory.createTitledBorder("Average Bandwidth"));
		
		responseTimePanel = new JPanel();
		responseTimePanel.setBorder(BorderFactory.createTitledBorder("Average Response Time"));
		
		costPanel = new JPanel();
		costPanel.setBorder(BorderFactory.createTitledBorder("Total Cost"));
		
		nrInstancePanel = new JPanel();
		nrInstancePanel.setBorder(BorderFactory.createTitledBorder("Nr of Instances"));
		
	}

	private void createControlPanelButtons() {
		startModelerButton = new JButton("Start");
		startModelerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startModelerButton.setEnabled(false);
				modeler.startModeler();
			}
		});
		
		stopModelerButton = new JButton("Stop");
	}

	private void createMaximumInstanceSection() {
		maxNrInstancesLbl = new JLabel("Maximum Number of Instances");
		maxInstanceText = new JTextField();
	}

	private void creatControlPanel() {
		controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 1));
		
		createCloudProviderConnectionSection();
		createControllerDesignSection();
		
		tabbedPane.addTab("Control Panel", controlPanel);
		
	}

	private void createControllerDesignSection() {
		controllerDesignPanel = new JPanel();
		controllerDesignPanel.setLayout(new GridLayout(6, 0));
		controllerDesignPanel.setBorder(BorderFactory.createTitledBorder("Controller Design"));
		
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
		senseLabel = new JLabel("Sense every (s)", JLabel.CENTER);
		
		senseSlider = new JSlider(JSlider.HORIZONTAL, SENSE_MIN, SENSE_MAX, SENSE_INIT);
		senseSlider.setMajorTickSpacing(20);
		senseSlider.setMinorTickSpacing(1);
		senseSlider.setPaintLabels(true);
		senseSlider.setPaintTicks(true);
		senseSlider.setPaintTrack(true);
		
		controllerDesignPanel.add(senseLabel);
		controllerDesignPanel.add(senseSlider);
	}

	private void createActSlider() {
		actLabel = new JLabel("Act every (s)", JLabel.CENTER);
		
		actSlider = new JSlider();
		actSlider = new JSlider(JSlider.HORIZONTAL, ACT_MIN, ACT_MAX, ACT_INIT);
		actSlider.setMajorTickSpacing(20);
		actSlider.setMinorTickSpacing(1);
		actSlider.setPaintLabels(true);
		actSlider.setPaintTicks(true);
		actSlider.setPaintTrack(true);
		
		controllerDesignPanel.add(actLabel);
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

	@Override
	public void saveSelectedSnapshotTo(File selectedFile) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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


}
