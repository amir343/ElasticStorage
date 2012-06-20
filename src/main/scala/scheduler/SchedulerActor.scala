package scheduler

import akka.actor.{ ActorLogging, ActorRef, Actor }
import akka.util.duration._
import protocol._
import protocol.CancellableSchedule
import protocol.Schedule

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
class SchedulerActor extends Actor with ActorLogging {

  val scheduler = context.system.scheduler

  val semiRealTimeSimulation = true

  def receive = {
    case Schedule(delay, actor, message)                                ⇒ schedule(delay, actor, message)
    case CancellableSchedule(delay, actor, message, cancellableMessage) ⇒ scheduleCancellable(delay, actor, message, cancellableMessage)
  }

  private def schedule(delay: Long, actor: ActorRef, message: Message) {
    semiRealTimeSimulation match {
      case true  ⇒ scheduler.scheduleOnce(delay milliseconds, actor, message)
      case false ⇒ //Implementation of simulated time
    }
  }

  private def scheduleCancellable(delay: Long, actor: ActorRef, message: Message, cancellableMessage: CancellableMessage) {
    semiRealTimeSimulation match {
      case true ⇒
        val cancellable = scheduler.scheduleOnce(delay milliseconds, actor, message)
        cancellableMessage match {
          case CancelProcess(pid, _) ⇒ sender ! CancelProcess(pid, cancellable)
          case z @ _                 ⇒ log.debug("Unrecognized cancellable message: %s".format(z))
        }
      case false ⇒ //Implementation of simulated time
    }
  }

}
