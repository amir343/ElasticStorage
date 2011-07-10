package econtroller.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class IPTextField extends JTextField {

	private static final long serialVersionUID = -8375291603355817165L;
	   final static String badchars = "`~!@#$%^&*()_+=\\|\"':;?/><, ";

	public IPTextField(int i) {
		super(i);
	}

	public IPTextField(String text) {
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
