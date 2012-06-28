///**
// * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package cloud;
//
//import cloud.api.CloudAPI;
//import cloud.common.CloudAPIInit;
//import cloud.common.ELB;
//import cloud.common.EPFD;
//import cloud.common.Generator;
//import cloud.elb.ElasticLoadBalancer;
//import cloud.epfd.HealthChecker;
//import cloud.gui.CloudGUI;
//import cloud.requestengine.RequestGenerator;
//import scenarios.manager.CloudConfiguration;
//import se.sics.kompics.*;
//import se.sics.kompics.address.Address;
//import se.sics.kompics.network.Network;
//import se.sics.kompics.network.mina.MinaNetwork;
//import se.sics.kompics.network.mina.MinaNetworkInit;
//import se.sics.kompics.timer.Timer;
//import se.sics.kompics.timer.java.JavaTimer;
//
///**
// *
// * @author Amir Moulavi
// * @date 2011-03-23
// *
// */
//
//public class CloudProvider extends ComponentDefinition {
//
//	private CloudConfiguration cloudConfiguration = CloudConfiguration.load(System.getProperty("cloudConfiguration"));
//	private CloudGUI gui;
//
//	public CloudProvider() {
//		bringUpGUI();
//
//		Component api = create(CloudAPI.class);
//		Component healthChecker = create(HealthChecker.class);
//		Component elb = create(ElasticLoadBalancer.class);
//		Component rg = create(RequestGenerator.class);
//		Component timer = create(JavaTimer.class);
//		Component network = create(MinaNetwork.class);
//
//		subscribe(handleFault, api.control());
//		subscribe(handleFault, healthChecker.control());
//		subscribe(handleFault, elb.control());
//		subscribe(handleFault, rg.control());
//		subscribe(handleFault, timer.control());
//		subscribe(handleFault, network.control());
//
//		Address self = cloudConfiguration.getSelfAddress();
//		CloudAPIInit init = new CloudAPIInit(cloudConfiguration, self);
//
//		trigger(init, api.control());
//		trigger(new MinaNetworkInit(self, 5), network.control());
//
//		connect(api.required(Network.class), network.provided(Network.class));
//		connect(api.required(Timer.class), timer.provided(Timer.class));
//		connect(api.required(EPFD.class), healthChecker.provided(EPFD.class));
//		connect(api.required(ELB.class), elb.provided(ELB.class));
//
//		connect(healthChecker.required(Network.class), network.provided(Network.class));
//		connect(healthChecker.required(Timer.class), timer.provided(Timer.class));
//
//		connect(elb.required(Network.class), network.provided(Network.class));
//		connect(elb.required(Generator.class), rg.provided(Generator.class));
//		connect(elb.required(Timer.class), timer.provided(Timer.class));
//
//		connect(rg.required(Timer.class), timer.provided(Timer.class));
//	}
//
//	private void bringUpGUI() {
//		gui = CloudGUI.getInstance();
//	}
//
//	Handler<Fault> handleFault = new Handler<Fault>() {
//		@Override
//		public void handle(Fault event) {
//			String error = generateString(event.getFault().toString(), event.getFault().getStackTrace());
//			gui.log("Error while running: " + error);
//		}
//
//		private String generateString(String beginning, StackTraceElement[] stackTrace) {
//			StringBuilder sb = new StringBuilder();
//			sb.append(beginning).append("\n");
//			for (int i=0; i<stackTrace.length; i++) {
//				sb.append("\tline ").append(stackTrace[i].getLineNumber()).append(" in class ");
//				sb.append(stackTrace[i].getClassName()).append("\n");
//			}
//			return sb.toString();
//		}
//	};
//
//}
