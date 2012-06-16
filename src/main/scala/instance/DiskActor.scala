package instance

import akka.actor.{ActorRef, ActorLogging, Actor}
import common.Block
import protocol._
import cloud.common.NodeConfiguration
import protocol.DiskReady
import protocol.LoadBlock
import protocol.DiskInit
import instance.os.Process


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
class DiskActor extends Actor with ActorLogging {

  private var diskReady:Boolean = false
 	private var _blocks = List.empty[Block]
 	private var enabled:Boolean = false
  private var instance:ActorRef = _


  def receive = {
    case DiskInit(nodeConfig)            => initialize(nodeConfig)
    case LoadBlock(blks)                 => loadBlocks(blks)
    case RestartSignal()                 => restart()
    case ReadBlock(id, process)          => readBlock(id, process)
  }

  private def initialize(nodeConfig:NodeConfiguration) {
    enabled = true
    instance = sender
    log.debug("Disk is started")
    instance ! DiskReady()
  }

  private def loadBlocks(blks:List[Block]) {
    if (enabled) {
      if (_blocks.size == 0) {
        _blocks = blks
        log.debug("%s Block(s) are ready for use".format(_blocks.size))
      }
      diskReady = true
    }
  }

  private def restart() {
    enabled = true
    log.debug("Disk received SHUTDOWN signal")
  }

  private def readBlock(id:String, process:Process) {
    _blocks.find( _.getName == id ) match {
      case Some(block) =>
        log.debug("Block %s is read".format(id))
        instance ! BlockResponse(block, process)

      case None        => log.error("Block %s does not exist on the disk".format(id))
    }
  }
}
