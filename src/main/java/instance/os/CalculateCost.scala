package instance.os

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}

/**
 * @author Amir Moulavi
 * @date 2011-07-16
 *
 */
class CalculateCost(request:ScheduleTimeout) extends Timeout(request)