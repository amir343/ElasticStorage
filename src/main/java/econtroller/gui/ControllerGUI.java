package econtroller.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import common.AbstractGUI;

import econtroller.controller.Controller;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ControllerGUI extends AbstractGUI {

	private static final long serialVersionUID = 8924494948307221974L;
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
		createLogPanel();
		setLayout(new GridLayout(1, 1));
		add(tabbedPane);
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
		controllerDesignPanel.setLayout(new GridLayout(2, 0));
		controllerDesignPanel.setBorder(BorderFactory.createTitledBorder("Controller Design"));
		
		createDesignSection();
		createDesignSectionButtons();
		
		controlPanel.add(controllerDesignPanel);		
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
		startControllerBtn.setEnabled(false);
		stopControllerBtn.setEnabled(true);
	}

	public void enableControllerDesignSection() {
		controllers.setEnabled(true);
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
		this.repaint();
	}

	public void startController() {
		controller.startController((String)controllers.getSelectedItem());		
	}

	public void stopController() {
		controller.stopController();
		
	}


}
