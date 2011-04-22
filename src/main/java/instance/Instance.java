package instance;

import instance.application.Application;
import instance.common.ApplicationInit;
import instance.common.CPUChannel;
import instance.common.CPUInit;
import instance.common.DiskChannel;
import instance.common.DiskInit;
import instance.common.MemChannel;
import instance.common.MemoryInit;
import instance.common.OSInit;
import instance.common.OSPort;
import instance.cpu.CPU;
import instance.disk.Disk;
import instance.gui.InstanceGUI;
import instance.mem.Memory;
import instance.os.OS;

import org.apache.log4j.PropertyConfigurator;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Fault;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkControl;
import se.sics.kompics.network.NetworkException;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import cloud.api.InstanceConfiguration;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-19
 *
 */

public class Instance extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}

	Positive<NetworkControl> netControl = requires(NetworkControl.class);
	
	InstanceConfiguration nodeConfiguration = InstanceConfiguration.load(System.getProperty("nodeConfiguration"));
	private InstanceGUI gui;
	
	public static void main(String[] args) {
		Kompics.createAndStart(Instance.class);
	}
	
	public Instance() {
		try {
			gui = InstanceGUI.getInstance();
			
			Component app = create(Application.class);
			Component os = create(OS.class);
			Component cpu = create(CPU.class);
			Component mem = create(Memory.class);
			Component disk = create(Disk.class);
			Component timer = create(JavaTimer.class);
			Component network = create(MinaNetwork.class);
	
			subscribe(handleFault, app.control());
			subscribe(handleFault, os.control());
			subscribe(handleFault, cpu.control());
			subscribe(handleFault, mem.control());
			subscribe(handleFault, disk.control());
			subscribe(handleFault, timer.control());
			subscribe(handleFault, network.control());
			subscribe(handleException, netControl);
			
			trigger(new ApplicationInit(), app.control());
			OSInit init = new OSInit(nodeConfiguration);
			trigger(init, os.control());
			trigger(new CPUInit(nodeConfiguration.getNodeConfiguration()), cpu.control());
			trigger(new MemoryInit(nodeConfiguration.getNodeConfiguration()), mem.control());
			trigger(new DiskInit(nodeConfiguration.getNodeConfiguration()), disk.control());
			trigger(new MinaNetworkInit(nodeConfiguration.getSelfAddress(), 5), network.control());
			
			connect(app.required(OSPort.class), os.provided(OSPort.class));
			connect(os.required(CPUChannel.class), cpu.provided(CPUChannel.class));
			connect(os.required(MemChannel.class), mem.provided(MemChannel.class));
			connect(os.required(DiskChannel.class), disk.provided(DiskChannel.class));
			connect(os.required(Timer.class), timer.provided(Timer.class));
			connect(cpu.required(Timer.class), timer.provided(Timer.class));
			connect(os.required(Network.class), network.provided(Network.class));
		} catch(Exception e) {
			gui.log(e.getMessage());
		}
	}
	
	Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault event) {
			String error = generateString(event.getFault().toString(), event.getFault().getStackTrace());
			gui.log("Error while running: " + error);
		}

		private String generateString(String beginning, StackTraceElement[] stackTrace) {
			StringBuilder sb = new StringBuilder();
			sb.append(beginning).append("\n");
			for (int i=0; i<stackTrace.length; i++) {
				sb.append("\tline ").append(stackTrace[i].getLineNumber()).append(" in class ");
				sb.append(stackTrace[i].getClassName()).append("\n");
			}
			return sb.toString();
		}
	};
	
	Handler<NetworkException> handleException = new Handler<NetworkException>() {
		@Override
		public void handle(NetworkException event) {
			gui.log("Got NetworkException");
		}
	};
	
}
