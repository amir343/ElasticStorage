package instance.os

import instance.common.Request
import java.util

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

case class Process(pid: String = util.UUID.randomUUID().toString,
                   request: Request = null,
                   blockSize: Long = 0,
                   remainingBlockSize: Long = 0,
                   timeout: Long = 0,
                   snapshot: Long = 0,
                   currentBandwidth: Long = 0)
