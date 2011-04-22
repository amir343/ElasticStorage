package instance.application;

import instance.common.ApplicationInit;
import instance.common.OSPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-03-18
 *
 */

public class Application extends ComponentDefinition {

	Positive<OSPort> os = requires(OSPort.class);
	
	public Application() {
		subscribe(initHandler, control);
	}
	
	Handler<ApplicationInit> initHandler = new Handler<ApplicationInit>() {
		@Override
		public void handle(ApplicationInit event) {

		}
	};
	
}
