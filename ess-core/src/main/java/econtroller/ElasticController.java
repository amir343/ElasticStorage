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
package econtroller;

import econtroller.actuator.Actuator;
import econtroller.actuator.ActuatorChannel;
import econtroller.actuator.ActuatorInit;
import econtroller.controller.Controller;
import econtroller.controller.ControllerInit;
import econtroller.gui.ControllerGUI;
import econtroller.modeler.ModelPort;
import econtroller.modeler.Modeler;
import econtroller.modeler.ModelerInit;
import econtroller.sensor.Sensor;
import econtroller.sensor.SensorChannel;
import econtroller.sensor.SensorInit;
import org.apache.log4j.PropertyConfigurator;
import se.sics.kompics.*;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ElasticController extends ComponentDefinition {
	
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	
	private ControllerConfiguration controllerConfiguration = ControllerConfiguration.load(System.getProperty("controllerConfiguration"));
	private ControllerGUI gui = ControllerGUI.getInstance();
	
	public ElasticController() {
		try {
			Component controller = create(Controller.class);
			Component sensor = create(Sensor.class);
			Component actuator = create(Actuator.class);
			Component timer = create(JavaTimer.class);
			Component modeler = create(Modeler.class);
			Component network = create(MinaNetwork.class);
			
			subscribe(handleFault, controller.control());
			subscribe(handleFault, sensor.control());
			subscribe(handleFault, actuator.control());
			subscribe(handleFault, modeler.control());
			subscribe(handleFault, timer.control());
			subscribe(handleFault, network.control());
			
			Address self = controllerConfiguration.getSelfAddress();
	
			trigger(new ControllerInit(controllerConfiguration), controller.control());
			trigger(new SensorInit(controllerConfiguration), sensor.control());
			trigger(new ActuatorInit(controllerConfiguration), actuator.control());
			trigger(new MinaNetworkInit(self, 5), network.control());
			trigger(new ModelerInit(self), modeler.control());
			
			connect(controller.required(Network.class), network.provided(Network.class));
			connect(controller.required(Timer.class), timer.provided(Timer.class));
			connect(controller.required(SensorChannel.class), sensor.provided(SensorChannel.class));
			connect(controller.required(ActuatorChannel.class), actuator.provided(ActuatorChannel.class));
			connect(controller.required(ModelPort.class), modeler.provided(ModelPort.class));
			
			connect(sensor.required(Network.class), network.provided(Network.class));
			connect(sensor.required(Timer.class), timer.provided(Timer.class));
			
			connect(modeler.required(Network.class), network.provided(Network.class));
			connect(modeler.required(Timer.class), timer.provided(Timer.class));
			
			connect(actuator.required(Network.class), network.provided(Network.class));
			
		} catch(Exception e) {
			gui.log(generateString(e.getMessage(), e.getStackTrace()));
		}
	}
	
	Handler<Fault> handleFault = new Handler<Fault>() {
		@Override
		public void handle(Fault event) {
			String error = generateString(event.getFault().toString(), event.getFault().getStackTrace());
			gui.log("Error while running: " + error);
		}

	};

	private String generateString(String beginning, StackTraceElement[] stackTrace) {
		StringBuilder sb = new StringBuilder();
		sb.append(beginning).append("\n");
		for (int i=0; i<stackTrace.length; i++) {
			sb.append("\tline ").append(stackTrace[i].getLineNumber()).append(" in class ");
			sb.append(stackTrace[i].getClassName()).append("\n");
		}
		return sb.toString();
	}

}
