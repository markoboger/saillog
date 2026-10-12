package saillog

import java.time.LocalDate
import saillog.io.*
import saillog.model.*

@main def lecture19(): Unit =
  val data = os.pwd / "data"
  val skipper = CrewMember("Marko", Role.Skipper)
  for file <- os.list(data).filter(f => f.ext == "gpx" || f.ext == "csv") do
    val track = if file.ext == "gpx" then Gpx.read(file) else Csv.read(file)
    track.fold(
      err => println(s"${file.last}: FAILED (${err.getMessage})"),
      pts => println(f"${file.last}: ${pts.size} points, ${Geo.distanceNm(pts)}%.2f nm"))

  val trips = List(
    Trip(1, 1, LocalDate.of(2026, 6, 14), "Konstanz – Meersburg", List(skipper),
         Gpx.read(data / "konstanz-meersburg.gpx").get),
    Trip(2, 2, LocalDate.of(2026, 7, 2), "Evening round", List(skipper),
         Csv.read(data / "abendrunde.csv").get))
  Json.save(os.pwd / "logbook.json", trips)
  val back = Json.load(os.pwd / "logbook.json")
  println(s"JSON round trip ok: ${back == trips}")
  println(f"nm per boat: ${Stats.nmPerBoat(trips)}")
  println(f"avg speed trip 1: ${Stats.avgSpeedKn(trips.head)}%.1f kn")
