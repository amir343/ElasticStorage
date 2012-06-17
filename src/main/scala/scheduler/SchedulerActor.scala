package scheduler

import akka.actor.{ ActorRef, Actor }
import akka.util.duration._
import protocol.{ Message, Schedule }

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
class SchedulerActor extends Actor {

  val scheduler = context.system.scheduler

  val semiRealTimeSimulation = true

  def receive = {
    case Schedule(delay, actor, message) ⇒ schedule(delay, actor, message)
  }

  private def schedule(delay: Long, actor: ActorRef, message: Message) {
    semiRealTimeSimulation match {
      case true  ⇒ scheduler.scheduleOnce(delay milliseconds, actor, message)
      case false ⇒ //Implementation of simulated time
    }
  }

}
