package instance.gui;

import instance.common.Block;
import instance.os.InstanceSnapshot;
import instance.os.OS;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * @author Amir Moulavi
 * @date 2011-07-14
 */
public interface GenericInstanceGUI {

    void cpuLoad(double load);
    void initializeDataBlocks(List<Block> blocks);
    void increaseNrDownloadersFor(String blockId);
    void decreaseNrDownloadersFor(String blockId);
    void resetNrDownloaders();
    void createCPULoadDiagram(JFreeChart chart);
    void createBandwidthDiagram(JFreeChart chart);
    void updateCPUInfoLabel(String info);
    void updateMemoryInfoLabel(String info);
    void updateBandwidthInfoLabel(String info);
    void setOSReference(OS os);
    void takeSnapshot();
    void addSnapshot(InstanceSnapshot snapshot);
    JTable getSnapshotTable();
    void deleteAllSnapshots();
    void saveAllSnapshotsTo(File selectedDir);
    void systemRestart();
    void restartOS();
    void decorateWhileSystemStartUp();
    void decorateSystemStarted();
    void updateCurrentCost(String cost);
    void updateTitle(String title);
}
