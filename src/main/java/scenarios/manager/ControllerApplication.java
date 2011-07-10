package scenarios.manager;

import econtroller.ControllerConfiguration;
import econtroller.ControllerProcess;
import econtroller.ElasticController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-11
 *
 */

public class ControllerApplication {
	
	private Class<? extends ElasticController> controllerClass;
	private ControllerConfiguration controllerConfiguration = new ControllerConfiguration();
	private final String classPath = System.getProperty("java.class.path");
	private File controllerConfigurationFile;

	public ControllerApplication(Class<? extends ElasticController> class1) {
		this.controllerClass = class1;
	}
	
	protected void controllerAddress(String ip, int port) {
		controllerConfiguration.setControllerAddress(ip, port);
	}
	
	public void start() {
		
		writeControllerConfigurationObjectIntoTempFile();
		startControllerProcess();
	}
	
	private void startControllerProcess() {
		ControllerProcess cp = new ControllerProcess(classPath, controllerConfigurationFile.getAbsolutePath(), controllerClass.getCanonicalName());
		cp.start();
	}
	
	private void writeControllerConfigurationObjectIntoTempFile() {
		File file = null;
		try {
			file = File.createTempFile("controllerConfiguration", ".bin");
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(controllerConfiguration);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		controllerConfigurationFile = file.getAbsoluteFile();
	}

}
