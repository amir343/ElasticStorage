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
