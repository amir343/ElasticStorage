package common

import java.awt.event.{ActionEvent, ActionListener}

/**
 * @author Amir Moulavi
 * @date 2011-07-17
 *
 */
class FilterActionListener(gui:AbstractGUI) extends ActionListener {

  override def actionPerformed(e:ActionEvent) = {
    gui filter
  }

}