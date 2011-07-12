package cloud.elb

import se.sics.kompics.timer.{Timeout, ScheduleTimeout}

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class UpdateELBTree(request:ScheduleTimeout) extends Timeout(request)