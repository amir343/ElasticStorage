package cloud.common

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}

/**
 * @author Amir Moulavi
 * @date 2011-07-09
 *
 */
class HeartbeatTimeout(request:ScheduleTimeout) extends Timeout(request)