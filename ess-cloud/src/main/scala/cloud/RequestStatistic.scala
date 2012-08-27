package cloud

case class RequestStatistic(start: Long, end: Long) {
  def responseTime = end - start

}