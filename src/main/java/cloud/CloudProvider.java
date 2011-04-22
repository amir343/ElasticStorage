package cloud;

import org.apache.log4j.PropertyConfigurator;

import scenarios.manager.CloudConfiguration;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Fault;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import cloud.api.CloudAPI;
import cloud.common.CloudAPIInit;
import cloud.common.ELB;
import cloud.common.EPFD;
import cloud.common.Generator;
import cloud.elb.ElasticLoadBalancer;
import cloud.epfd.HealthChecker;
import cloud.gui.CloudGUI;
import cloud.requestengine.RequestGenerator;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-23
 *
 */

public class CloudProvider extends ComponentDefinition {

	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	
	private CloudConfiguration cloudConfiguration = CloudConfiguration.load(System.getProperty("cloudConfiguration"));
	private CloudGUI gui;
	
	public static void main(String[] args) {
		Kompics.createAndStart(CloudProvider.class);
	}
	
	public CloudProvider() {
		bringUpGUI();

		Component api = create(CloudAPI.class);
		Component healthChecker = create(HealthChecker.class);
		Component elb = create(ElasticLoadBalancer.class);
		Component rg = create(RequestGenerator.class);
		Component timer = create(JavaTimer.class);
		Component network = create(MinaNetwork.class);

		subscribe(handleFault, api.control());
		subscribe(handleFault, healthChecker.control());
		subscribe(handleFault, elb.control());
		subscribe(handleFault, rg.control());
		subscribe(handleFault, timer.control());
		subscribe(handleFault, network.control());
		
		Address self = cloudConfiguration.getSelfAddress(); 
		CloudAPIInit init = new CloudAPIInit(cloudConfiguration, self);
		
		trigger(init, api.control());
		trigger(new MinaNetworkInit(self, 5), network.control());
		
		connect(api.required(Network.class), network.provided(Network.class));
		connect(api.required(Timer.class), timer.provided(Timer.class));
		connect(api.required(EPFD.class), healthChecker.provided(EPFD.class));
		connect(api.required(ELB.class), elb.provided(ELB.class));
		
		connect(healthChecker.required(Network.class), network.provided(Network.class));
		connect(healthChecker.required(Timer.class), timer.provided(Timer.class));
		
		connect(elb.required(Network.class), network.provided(Network.class));
		connect(elb.required(Generator.class), rg.provided(Generator.class));
		
		connect(rg.required(Timer.class), timer.provided(Timer.class));
	}

	private void bringUpGUI() {
		gui = CloudGUI.getInstance();
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
	
}
