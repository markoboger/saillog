package saillog.model

import java.time.{Instant, LocalDate}
import upickle.default.*

given ReadWriter[Instant]   = readwriter[String].bimap(_.toString, Instant.parse)
given ReadWriter[LocalDate] = readwriter[String].bimap(_.toString, LocalDate.parse)

enum BoatKind derives ReadWriter:
  case Dinghy, Keelboat, Catamaran

enum Role derives ReadWriter:
  case Skipper, Crew

case class Boat(id: Int, name: String, kind: BoatKind) derives ReadWriter
case class CrewMember(name: String, role: Role) derives ReadWriter
case class TrackPoint(lat: Double, lon: Double, time: Instant) derives ReadWriter

case class Trip(id: Int, boatId: Int, date: LocalDate, title: String,
                crew: List[CrewMember], track: List[TrackPoint]) derives ReadWriter
