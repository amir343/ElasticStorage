package instance.common

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}

/**
 * @author Amir Moulavi
 *
 */
class LoadCalculationTimeout(request:ScheduleTimeout) extends Timeout(request)