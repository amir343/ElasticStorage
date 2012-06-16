/**
 * Copyright 2011 Amir Moulavi (amir.moulavi@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package econtroller.fuzzy

import logger.{ Logger, LoggerFactory }
import econtroller.gui.ControllerGUI

/**
 * @author Amir Moulavi
 * @date 2011-07-23
 *
 */
class FuzzyInferenceEngine {

  private val logger: Logger = LoggerFactory.getLogger(classOf[FuzzyInferenceEngine], ControllerGUI.getInstance())

  def act(scaleUp: Boolean, averageCpuSTD: Double): Boolean = (scaleUp, averageCpuSTD) match {
    case (false, std) if high(std) ⇒ false
    case (_, _)                    ⇒ true
  }

  def high(std: Double): Boolean = std match {
    case s if s > 20.0 ⇒ true
    case _             ⇒ false
  }

}