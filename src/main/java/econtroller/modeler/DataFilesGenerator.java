package econtroller.modeler;

import cloud.common.TrainingData;
import econtroller.gui.ControllerGUI;
import logger.Logger;
import logger.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Amir Moulavi
 *
 */

public class DataFilesGenerator {

	private Logger logger = LoggerFactory.getLogger(DataFilesGenerator.class, ControllerGUI.getInstance());
	
	private List<Integer> nn = new ArrayList<Integer>();
	private List<Double> tp = new ArrayList<Double>();
	private List<Double> cpu = new ArrayList<Double>();
	private List<Double> bw = new ArrayList<Double>();
	private List<Double> tc = new ArrayList<Double>();
	private List<Double> rt = new ArrayList<Double>();
	
	private File cpuDump = new File("dumps/cpu.dump");
	private File bwDump = new File("dumps/bw.dump");
	private File tcDump = new File("dumps/tc.dump");
	private File rtDump = new File("dumps/rt.dump");
	
	public void add(TrainingData td) {
		tp.add(td.getThroughputMean());
		cpu.add(td.getCPULoadMean());
		nn.add(td.getNrNodes());
		bw.add(td.getBandwidthMean());
		tc.add(td.getTotalCost());
		rt.add(td.getResponseTimeMean());
	}

	public void clear() {
		tp.clear();
		cpu.clear();
		nn.clear();
		bw.clear();
		tc.clear();
		rt.clear();		
	}
	
	public void dump() {
		generateCPUdumpFile();
		generateBWDumpFile();
		generateTCDumpFile();
		generateRTDumpFile();
	}

	private void generateRTDumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(rt.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(cpu.get(i-1)).append(" ").append(bw.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
		try {
			FileUtils.write(rtDump, sb.toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}

	private void generateTCDumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(tc.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(bw.get(i-1)).append(" ").append(tc.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
		try {
			FileUtils.write(tcDump, sb.toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}

	private void generateBWDumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(bw.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(bw.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
		try {
			FileUtils.write(bwDump, sb.toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}

	private void generateCPUdumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(cpu.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(cpu.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
		try {
			FileUtils.write(cpuDump, sb.toString());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}

}
