package econtroller.controller

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}

/**
 * @author Amir Moulavi
 *
 */
class ActuateTimeout(request:ScheduleTimeout) extends Timeout(request)