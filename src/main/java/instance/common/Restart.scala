package instance.common

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}

/**
 * @author Amir Moulavi
 *
 */
class Restart(request:ScheduleTimeout) extends Timeout(request)