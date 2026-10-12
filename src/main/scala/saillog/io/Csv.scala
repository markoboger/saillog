package saillog.io

import java.time.Instant
import scala.util.Try
import saillog.model.TrackPoint

object Csv:
  def parse(lines: Seq[String]): List[TrackPoint] =
    lines.drop(1).map(_.split(",")).collect {
      case Array(t, lat, lon) =>
        TrackPoint(lat.toDouble, lon.toDouble, Instant.parse(t))
    }.toList

  def read(file: os.Path): Try[List[TrackPoint]] = Try(parse(os.read.lines(file)))
