package instance.gui;

import common.AbstractGUI;
import common.GUI;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
