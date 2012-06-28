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

import common.AbstractGUI;
import common.GUI;

import java.io.File;

/**
 * @author Amir Moulavi
 * @date 2011-07-14
 */
public class DummyInstanceGUI implements GUI {

    public DummyInstanceGUI() {

    }

    @Override
    public AbstractGUI getGUIComponent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(String text) {
/*
        try {
            File file = File.createTempFile("instanceLog", ".log");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            dos.writeUTF(text);
            dos.close();
        } catch (IOException e) {

        }
*/

    }

    @Override
    public void saveLogFileTo(File selectedDir) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveSelectedSnapshotTo(File selectedFile) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void lockLogText() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unlockLogText() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
