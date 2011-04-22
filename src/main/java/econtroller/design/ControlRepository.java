package econtroller.design;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logger.Logger;
import logger.LoggerFactory;
import econtroller.gui.ControllerGUI;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-18
 *
 */

public class ControlRepository {

	private static ControlRepository instance = new ControlRepository();
	
	public static ControlRepository getInstance() {
		return instance;
	}
	
	private Logger logger = LoggerFactory.getLogger(ControlRepository.class, ControllerGUI.getInstance());
	private Map<String, ControllerDesign> klasses = new HashMap<String, ControllerDesign>();
	private int nrOfSearchedClasses = 0;
	
	private ControlRepository() {
		searchPackages();
	}
	
	private boolean hasImplementedControllerDesignInterface(Class<?>[] interfaces) {
		for (Class<?> klass : interfaces) {
			if (klass.equals(ControllerDesign.class))
				return true;
		}
		return false;
	}

	public List<String> getControllerNames() {
		List<String> controllers = new ArrayList<String>();
		for (String name : klasses.keySet()) {
			controllers.add(name);
		}
		return controllers;
	}
	
	public void searchPackages() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			Enumeration<URL> resources;
			try {
				resources = loader.getResources(".");
				URL url = resources.nextElement();
				File dirs = new File(url.getPath());
				investigate(dirs);
				logger.info("[" + nrOfSearchedClasses + "] classes searched and found [" + klasses.size() + "] controller(s)");
			} catch (IOException e) {
				logger.error(e.getMessage());
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
			} catch (InstantiationException e) {
				logger.error(e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	private void investigate(File file) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (file.isDirectory()) {
			for (String str : file.list()) {
				investigate(new File(file.getPath() + File.separatorChar + str));
			}
		} else {
			String fileName = file.getName();
			String packagePrefix = file.getPath().substring(file.getPath().indexOf("classes") + 8, file.getPath().length()).replace("/", ".");
			if (fileName.endsWith(".class")) {
				if (fileName.toLowerCase().indexOf("gui") < 0) {
					Class<?> klass = Class.forName(packagePrefix.substring(0, packagePrefix.length() - 6));
					if (hasImplementedControllerDesignInterface(klass.getInterfaces())) {
						klasses.put(klass.getSimpleName(), (ControllerDesign)klass.newInstance());
						System.out.println(klass.getSimpleName());
					}
				}
				nrOfSearchedClasses++;
			}			
		}
		
	}

	public ControllerDesign getControllerWithName(String selectedController) {
		return klasses.get(selectedController).clone();
	}
	
}
