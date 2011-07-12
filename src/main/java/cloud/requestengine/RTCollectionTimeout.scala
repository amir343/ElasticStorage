package cloud.requestengine

import se.sics.kompics.timer.{ScheduleTimeout, Timeout}

/**
 * @author Amir Moulavi
 * @date 2011-07-12
 *
 */
class RTCollectionTimeout(request:ScheduleTimeout) extends Timeout(request)