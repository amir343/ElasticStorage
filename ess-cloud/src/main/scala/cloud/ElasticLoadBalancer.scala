package cloud

import akka.actor.{ ActorLogging, Actor }
import protocol.ELBInit

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

class ElasticLoadBalancer extends Actor with ActorLogging {

  def receive = genericHandler orElse
    uncategorizedHandler

  def genericHandler: Receive = {
    case ELBInit(cloudConfig) ⇒ //TODO
  }

  def uncategorizedHandler: Receive = {
    case z @ _ ⇒ log.debug("Received uncategorized message %s".format(z))
  }

}