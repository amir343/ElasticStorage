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
package cloud.gui;

import logger.Logger;
import logger.LoggerFactory;
import org.apache.commons.io.FileUtils;
import statistics.distribution.DistributionName;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class DistributionSelectionActionListener implements ActionListener {

	private CloudGUI gui;
	private Logger logger;

	public DistributionSelectionActionListener(CloudGUI cloudGUI) {
		this.gui = cloudGUI;
		logger = LoggerFactory.getLogger(CloudGUI.class, gui);
	}

	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent e) {
		String name = (String) gui.getDistributions().getSelectedItem();
		if (DistributionName.EXPONENTIAL.getName().equals(name)) {
			gui.decorateExponentialDistribution();
		} else if (DistributionName.UNIFORM.getName().equals(name)) {
			gui.decorateForUniformDistribution();
		} else if (DistributionName.CONSTANT.getName().equals(name)) {
			gui.decorateForConstantDistribution();
		} else if (DistributionName.CUSTOM.getName().equals(name)) {
			JFileChooser fileChooser = new JFileChooser();
			int returnVal = fileChooser.showOpenDialog(gui);
			if (returnVal == fileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				try {
					List<String> lines = FileUtils.readLines(file);
					boolean result = validate(lines);
					if (!result) {
						gui.decorateForInValidDistribution();
						logger.error("Invalid Distribution file " + file.getName());
					} else {
						gui.decorateForCustomDistribution(lines);
						logger.info("File " + file.getName() + " is imported to generate the distribution from" );
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				gui.getDistributions().setSelectedIndex(0);
				gui.decorateForUniformDistribution();
			}
		}
	}

	private boolean validate(List<String> lines) {
		if (lines.size() == 0) return false;
		for (String line : lines) {
			try {
				Integer.parseInt(line.trim());
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

}
