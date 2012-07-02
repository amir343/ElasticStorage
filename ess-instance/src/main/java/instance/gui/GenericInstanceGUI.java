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
package instance.gui;

import common.GUI;
import instance.InstanceActor;
import instance.common.Block;
import instance.os.InstanceSnapshot;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * @author Amir Moulavi
 * @date 2011-07-14
 */
public interface GenericInstanceGUI extends GUI {

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
    void updateSimultaneousDownloads(String info);
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
    void updateCurrentTransfers(int size);
    void updateRequestQueue(int n);
    void setInstanceReference(InstanceActor actor);
    void disposeGUI();
}
