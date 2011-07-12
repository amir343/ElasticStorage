package econtroller.modeler;

import cloud.common.TrainingData;
import econtroller.gui.ControllerGUI;
import logger.Logger;
import logger.LoggerFactory;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private File nnStateDump;
    private File tpStateDump;
    private File cpuStateDump;
    private File bwStateDump;
    private File tcStateDump;
    private File rtStateDump;

	private File cpuDump;
	private File bwDump;
	private File tcDump;
	private File rtDump;
	
	public void add(TrainingData td) {
		tp.add(td.getThroughputMean());
		cpu.add(td.getCpuLoadMean());
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
        prepareFiles();
		generateCPUdumpFile();
		generateBWDumpFile();
		generateTCDumpFile();
		generateRTDumpFile();
        generateStateDumpFile(nnStateDump, nn);
        generateStateDumpFile(tpStateDump, tp);
        generateStateDumpFile(cpuStateDump, cpu);
        generateStateDumpFile(bwStateDump, bw);
        generateStateDumpFile(tcStateDump, tc);
        generateStateDumpFile(rtStateDump, rt);
	}

    private void prepareFiles() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        String folder = simpleDateFormat.format(Calendar.getInstance().getTime());
        nnStateDump = new File("dumps/ " + folder + "/input_nn_state.dump");
        tpStateDump = new File("dumps/ " + folder + "/input_tp_state.dump");
        cpuStateDump = new File("dumps/ " + folder + "/output_cpu_state.dump");
        bwStateDump = new File("dumps/ " + folder + "/output_bw_state.dump");
        tcStateDump = new File("dumps/ " + folder + "/output_tc_state.dump");
        rtStateDump = new File("dumps/ " + folder + "/output_rt_state.dump");
        cpuDump = new File("dumps/ " + folder + "/cpu.dump");
        bwDump = new File("dumps/ " + folder + "/bw.dump");
        tcDump = new File("dumps/ " + folder + "/tc.dump");
        rtDump = new File("dumps/ " + folder + "/rt.dump");
    }

    private void generateStateDumpFile(File dumpFile, List data) {
        StringBuilder sb = new StringBuilder();
        for (int i=1; i<data.size(); i++) {
            sb.append(data.get(i));
            if (i != data.size() - 1) sb.append("\n");
        }
        writeToFile(dumpFile, sb.toString());
    }

    private void generateRTDumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(rt.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(cpu.get(i-1)).append(" ").append(bw.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
        writeToFile(rtDump, sb.toString());
	}

	private void generateTCDumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(tc.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(bw.get(i-1)).append(" ").append(tc.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
		writeToFile(tcDump, sb.toString());
	}

	private void generateBWDumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(bw.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(bw.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
		writeToFile(bwDump, sb.toString());
	}

	private void generateCPUdumpFile() {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<nn.size(); i++) {
			sb.append(cpu.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(cpu.get(i-1)).append(" ").append(nn.get(i-1));
			if (i != nn.size() - 1) sb.append("\n");
		}
        writeToFile(cpuDump, sb.toString());
	}

    private void writeToFile(File file, String str) {
        try {
            FileUtils.write(file, str);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
