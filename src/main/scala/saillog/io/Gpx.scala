package saillog.io

import java.time.Instant
import scala.util.Try
import scala.xml.XML
import saillog.model.TrackPoint

object Gpx:
  def parse(xml: String): List[TrackPoint] =
    val doc = XML.loadString(xml)
    (doc \\ "trkpt").map { p =>
      TrackPoint((p \@ "lat").toDouble, (p \@ "lon").toDouble,
                 Instant.parse((p \ "time").text))
    }.toList

  def read(file: os.Path): Try[List[TrackPoint]] = Try(parse(os.read(file)))
