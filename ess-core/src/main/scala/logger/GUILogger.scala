package logger

import common.GUI

/**
 * Copyright 2012 Amir Moulavi (amir.moulavi@gmail.com)
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
 *
 * @author Amir Moulavi
 */
class GUILogger(gui: GUI, className: Class[_]) {

  private val start = System.currentTimeMillis()

  def log(text: AnyRef, level: String) {
    var log: String = null
    if (level != null) log = "[%010.4f] %s {%s} %s".format(now, level, className, text)
    else log = "[%010.4f] %s".format(now, text)
    gui.log(log)
  }

  def raw(text: AnyRef) {
    log(text, null)
  }

  def info(text: AnyRef) {
    log(text, "INFO")
  }

  def debug(text: AnyRef) {
    log(text, "DEBUG")
  }

  def getTime: Double = now

  def warn(text: AnyRef) {
    log(text, "WARN")
  }

  def error(text: AnyRef) {
    log(text, "ERROR")
  }

  private def now: Double = {
    val now: Long = System.currentTimeMillis
    val diff: Long = now - start
    diff.doubleValue / 1000
  }

  def nowInSeconds: Long = {
    val now: Long = System.currentTimeMillis - start
    now / 1000
  }
}
