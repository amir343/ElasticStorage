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
//package econtroller.modeler;
//
//import cloud.common.TrainingData;
//import cloud.elb.SenseData;
//import econtroller.gui.ControllerGUI;
//import logger.Logger;
//import logger.LoggerFactory;
//import org.apache.commons.io.FileUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
///**
// * @author Amir Moulavi
// */
//public class DataFilesGenerator2 {
//
//    private Logger logger = LoggerFactory.getLogger(DataFilesGenerator.class, ControllerGUI.getInstance());
//
//    private List<Integer> nn = new ArrayList<Integer>();
//    private List<Double> tp = new ArrayList<Double>();
//    private List<Double> cpuLoad = new ArrayList<Double>();
//    private List<Double> bandwidth = new ArrayList<Double>();
//    private List<Double> tc = new ArrayList<Double>();
//    private List<Double> rt = new ArrayList<Double>();
//
//    private File nnStateDump;
//    private File tpStateDump;
//    private File cpuLoadStateDump;
//    private File bandwidthStateDump;
//    private File tcStateDump;
//    private File rtStateDump;
//
//    private File meDump;
//
//    private File cpuLoadDump;
//    private File bandwidthDump;
//    private File tcDump;
//    private File rtDump;
//
//    private double cpuCoefficient = 1.02024267035455;
//    private double tcCoefficient = 0;
//    private double rtCoefficient = 1.87196306940221e-06;
//    private double nnCoefficient = 2.3004;
//
//    public void add(TrainingData td) {
//        nn.add(td.getNrNodes());
//        cpuLoad.add(td.getCpuLoadMean());
//        bandwidth.add(td.getBandwidthMean());
//        tp.add(td.getThroughputMean());
//        tc.add(td.getPeriodicTotalCost());
//        rt.add(td.getResponseTimeMean());
//    }
//
//    public void add(SenseData senseData) {
//        nn.add(senseData.getNrNodes());
//        cpuLoad.add(senseData.getCpuLoadMean());
//        bandwidth.add(senseData.getBandwidthMean());
//        tp.add(senseData.getThroughputMean());
//        tc.add(senseData.getPeriodicTotalCost());
//        rt.add(senseData.getResponseTimeMean());
//    }
//
//    public void clear() {
//        tp.clear();
//        cpuLoad.clear();
//        nn.clear();
//        bandwidth.clear();
//        tc.clear();
//        rt.clear();
//    }
//
//    public void dump() {
//        prepareFiles();
//        generateCpuLoadDumpFile();
//        generateBandwidthDumpFile();
//        generateTCDumpFile();
//        generateRTDumpFile();
//        generateStateDumpFile(nnStateDump, nn);
//        generateStateDumpFile(tpStateDump, tp);
//        generateStateDumpFile(cpuLoadStateDump, cpuLoad);
//        generateStateDumpFile(bandwidthStateDump, bandwidth);
//        generateStateDumpFile(tcStateDump, tc);
//        generateStateDumpFile(rtStateDump, rt);
//        generateModelEvaluation(meDump, cpuLoad);
//    }
//
//    private void prepareFiles() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");
//        String folder = simpleDateFormat.format(Calendar.getInstance().getTime());
//        nnStateDump = new File("dumps/" + folder + "/input_nn_state.dump");
//        tpStateDump = new File("dumps/" + folder + "/input_tp_state.dump");
//        cpuLoadStateDump = new File("dumps/" + folder + "/output_cpu_load_state.dump");
//        bandwidthStateDump = new File("dumps/" + folder + "/output_bw_state.dump");
//        tcStateDump = new File("dumps/" + folder + "/output_tc_state.dump");
//        rtStateDump = new File("dumps/" + folder + "/output_rt_state.dump");
//        cpuLoadDump = new File("dumps/" + folder + "/cpuLoad.dump");
//        bandwidthDump = new File("dumps/" + folder + "/bw.dump");
//        tcDump = new File("dumps/" + folder + "/tc.dump");
//        rtDump = new File("dumps/" + folder + "/rt.dump");
//        meDump = new File("dumps/" + folder + "/modelEvaluation.dump");
//    }
//
//    private void generateModelEvaluation(File meDump, List<Double> cpuLoad) {
//        StringBuilder sb = new StringBuilder();
//        double predicted;
//        for (int i=1; i<cpuLoad.size(); i++) {
//            if (cpuLoad.get(i) == 0.0) continue;
//            predicted = cpuCoefficient*cpuLoad.get(i-1) + tcCoefficient*tc.get(i-1) + rtCoefficient*rt.get(i-1) + nnCoefficient*nn.get(i-1) - 10;
//            sb.append(cpuLoad.get(i)).append(" ").append(predicted).append("\n");
//        }
//        writeToFile(meDump, sb.toString());
//    }
//
//    private void generateStateDumpFile(File dumpFile, List data) {
//        StringBuilder sb = new StringBuilder();
//        for (int i=1; i<data.size(); i++) {
//            sb.append(data.get(i));
//            if (i != data.size() - 1) sb.append("\n");
//        }
//        writeToFile(dumpFile, sb.toString());
//    }
//
//    private void generateRTDumpFile() {
//        StringBuilder sb = new StringBuilder();
//        for (int i=1; i<nn.size(); i++) {
//            sb.append(rt.get(i)).append(" ").append(cpuLoad.get(i-1)).append(" ").append(rt.get(i-1)).append(" ").append(nn.get(i - 1));
//            if (i != nn.size() - 1) sb.append("\n");
//        }
//        writeToFile(rtDump, sb.toString());
//    }
//
//    private void generateTCDumpFile() {
//        StringBuilder sb = new StringBuilder();
//        for (int i=1; i<nn.size(); i++) {
//            sb.append(tc.get(i)).append(" ").append(tc.get(i - 1)).append(" ").append(nn.get(i - 1));
//            if (i != nn.size() - 1) sb.append("\n");
//        }
//        writeToFile(tcDump, sb.toString());
//    }
//
//    private void generateBandwidthDumpFile() {
//        StringBuilder sb = new StringBuilder();
//        for (int i=1; i<nn.size(); i++) {
//            sb.append(bandwidth.get(i)).append(" ").append(tp.get(i-1)).append(" ").append(bandwidth.get(i-1)).append(" ").append(nn.get(i-1));
//            if (i != nn.size() - 1) sb.append("\n");
//        }
//        writeToFile(bandwidthDump, sb.toString());
//    }
//
//    private void generateCpuLoadDumpFile() {
//        StringBuilder sb = new StringBuilder();
//        for (int i=1; i<nn.size(); i++) {
//            sb.append(cpuLoad.get(i)).append(" ").append(cpuLoad.get(i - 1)).append(" ").append(nn.get(i - 1));
//            if (i != nn.size() - 1) sb.append("\n");
//        }
//        writeToFile(cpuLoadDump, sb.toString());
//    }
//
//    private void writeToFile(File file, String str) {
//        try {
//            FileUtils.write(file, str);
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//    }
//
//}
