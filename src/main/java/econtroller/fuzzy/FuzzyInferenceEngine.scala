package econtroller.fuzzy

import logger.{Logger, LoggerFactory}
import econtroller.gui.ControllerGUI

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class FuzzyInferenceEngine {

  private val logger:Logger = LoggerFactory.getLogger(classOf[FuzzyInferenceEngine], ControllerGUI.getInstance())

  def act(scaleUp:Boolean, averageCpuSTD:Double):Boolean = (scaleUp, averageCpuSTD) match {
    case (false, std) if high(std) => false
    case (_, _) => true
  }

  def high(std:Double):Boolean = std match {
    case s if s > 20.0 => true
    case _ => false
  }

}