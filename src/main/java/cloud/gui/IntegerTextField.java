package cloud.gui;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * 
 * @author Amir Moulavi
 * @date 2011-04-02
 *
 */

public class IntegerTextField extends JTextField {

	private static final long serialVersionUID = -8375291603355817165L;
	   final static String badchars = "`~!@#$%^&*()_+=\\|\"':;?/><, ";

    public IntegerTextField(int i) {
		super(i);
	}

	public IntegerTextField(String text) {
		super(text);
	}

	public void processKeyEvent(KeyEvent ev) {

        char c = ev.getKeyChar();

        if((Character.isLetter(c) && !ev.isAltDown()) 
           || badchars.indexOf(c) > -1) {
            ev.consume();
            return;
        }
        
        if(c == '-' && getDocument().getLength() > 0) 
        	ev.consume();
        else super.processKeyEvent(ev);

    }

}
