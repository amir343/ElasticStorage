package instance

sealed trait Size {
  val size: Long

  def sizeString(size: Long): String = size match {
    case s if size >= GB.size ⇒ "%s GB" format s.toDouble / GB.size.toDouble
    case s if size >= MB.size ⇒ "%s MB" format s.toDouble / MB.size.toDouble
    case s if size >= KB.size ⇒ "%s KB" format s.toDouble / KB.size.toDouble
    case _                    ⇒ "%s B" format size
  }

}
case object KB extends Size { override val size: Long = 1024L }
case object MB extends Size { override val size: Long = 1048576L }
case object GB extends Size { override val size: Long = 1073741824L }
case object GHertz extends Size { override val size: Long = 1000000000L }

object Size {
  def cpuHertzString(size: Long): String = "%s GHz" format (size / GHertz.size).asInstanceOf[Double]
  def cpuSpeed(speed: Double): Long = speed.toLong * GHertz.size
  def sizeString(size: Long): String = size match {
    case s if size >= GB.size ⇒ "%s GB" format s.toDouble / GB.size.toDouble
    case s if size >= MB.size ⇒ "%s MB" format s.toDouble / MB.size.toDouble
    case s if size >= KB.size ⇒ "%s KB" format s.toDouble / KB.size.toDouble
    case _                    ⇒ "%s B" format size
  }
}