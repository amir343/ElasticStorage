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
package econtroller.design;

import econtroller.gui.ControllerGUI;
import logger.Logger;
import logger.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-18
 *
 */

public class ControlRepository {

	private static ControlRepository instance = new ControlRepository();
    private int scalaClass = 0;
    private int javaClass = 0;

    public static ControlRepository getInstance() {
		return instance;
	}
	
	private Logger logger = LoggerFactory.getLogger(ControlRepository.class, ControllerGUI.getInstance());
	private Map<String, ControllerDesign> klasses = new HashMap<String, ControllerDesign>();
	private int nrOfSearchedClasses = 0;
	
	private ControlRepository() {
		searchPackages();
	}
	
	public List<String> getControllerNames() {
		List<String> controllers = new ArrayList<String>();
		for (String name : klasses.keySet()) {
			controllers.add(name);
		}
        Collections.sort(controllers);
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
				logger.info("[" + scalaClass + "] \"Scala\" classes and [" + javaClass + "] \"Java\" classes found");
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
                if (!fileName.toLowerCase().contains("gui")) {
                    Class<?> klass = Class.forName(packagePrefix.substring(0, packagePrefix.length() - 6));
                    if (hasImplementedControllerDesignInterface(klass.getInterfaces())) {
						klasses.put(klass.getSimpleName(), (ControllerDesign) klass.newInstance());
                    }
                    countScalaOrJava(klass.getInterfaces());
                } else {
                    javaClass++;
                }
				nrOfSearchedClasses++;
			}
		}

	}

    private boolean hasImplementedControllerDesignInterface(Class<?>[] interfaces) {
        for (Class<?> klass : interfaces) {
            if (klass.equals(ControllerDesign.class))
                return true;
        }
        return false;
    }

    private void countScalaOrJava(Class<?>[] interfaces) {
        boolean found = false;
        for(Class<?> klass : interfaces) {
            if (klass.toString().contains("scala")) {
                scalaClass++;
                found = true;
                break;
            }
        }
        if (!found) javaClass++;
    }

    public ControllerDesign getControllerWithName(String selectedController) {
		return klasses.get(selectedController).clone();
	}
	
}
