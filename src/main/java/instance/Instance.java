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
package instance;

import cloud.api.InstanceConfiguration;
import instance.common.*;
import instance.cpu.CPU;
import instance.disk.Disk;
import instance.gui.DummyInstanceGUI;
import instance.gui.GenericInstanceGUI;
import instance.gui.HeadLessGUI;
import instance.gui.InstanceGUI;
import instance.mem.Memory;
import instance.os.OS;
import org.apache.log4j.PropertyConfigurator;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkControl;
import se.sics.kompics.network.NetworkException;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

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
	private GenericInstanceGUI gui;
	
	public Instance() {
		try {
            boolean headless = nodeConfiguration.getNodeConfiguration().getHeadLess();
			if (!headless)
                gui = InstanceGUI.getInstance();
            else
                gui = new HeadLessGUI();

			Component os = create(OS.class);
			Component cpu = create(CPU.class);
			Component mem = create(Memory.class);
			Component disk = create(Disk.class);
			Component timer = create(JavaTimer.class);
			Component network = create(MinaNetwork.class);
	
			subscribe(handleFault, os.control());
			subscribe(handleFault, cpu.control());
			subscribe(handleFault, mem.control());
			subscribe(handleFault, disk.control());
			subscribe(handleFault, timer.control());
			subscribe(handleFault, network.control());
			subscribe(handleException, netControl);
			
			OSInit init = new OSInit(nodeConfiguration);
			trigger(init, os.control());
			trigger(new CPUInit(nodeConfiguration.getNodeConfiguration()), cpu.control());
			trigger(new MemoryInit(nodeConfiguration.getNodeConfiguration()), mem.control());
			trigger(new DiskInit(nodeConfiguration.getNodeConfiguration()), disk.control());
			trigger(new MinaNetworkInit(nodeConfiguration.getSelfAddress(), 5), network.control());
			
			connect(os.required(CPUChannel.class), cpu.provided(CPUChannel.class));
			connect(os.required(MemChannel.class), mem.provided(MemChannel.class));
			connect(os.required(DiskChannel.class), disk.provided(DiskChannel.class));
			connect(os.required(Timer.class), timer.provided(Timer.class));
			connect(cpu.required(Timer.class), timer.provided(Timer.class));
			connect(os.required(Network.class), network.provided(Network.class));
		} catch(Exception e) {
			if (gui instanceof InstanceGUI) InstanceGUI.getInstance().log(e.getMessage());
		}
	}
	
	Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault event) {
			String error = generateString(event.getFault().toString(), event.getFault().getStackTrace());
			if (gui instanceof InstanceGUI) InstanceGUI.getInstance().log("Error while running: " + error);
            else (new DummyInstanceGUI()).log("Error while running: " + error);
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
			if (gui instanceof InstanceGUI) InstanceGUI.getInstance().log("Got NetworkException");
            else (new DummyInstanceGUI()).log("Got NetworkException");
		}
	};
	
}
