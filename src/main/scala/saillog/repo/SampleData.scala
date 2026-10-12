package saillog.repo

import java.time.LocalDate
import saillog.io.*
import saillog.model.*

object SampleData:
  def fill(repo: LogbookRepository, data: os.Path): Unit =
    val marko = CrewMember("Marko", Role.Skipper)
    val anna  = CrewMember("Anna", Role.Crew)
    // Both boats belong to HTWG and are used for student training:
    // Dock for the motorboat licence, the X99 Expresso for sailing.
    repo.addBoat(Boat(1, "Dock", BoatKind.Motorboat))
    repo.addBoat(Boat(2, "X99 Expresso", BoatKind.Keelboat))
    repo.addTrip(Trip(1, 1, LocalDate.of(2025, 6, 14), "Konstanz – Meersburg",
      List(marko), Gpx.read(data / "konstanz-meersburg.gpx").get))
    repo.addTrip(Trip(2, 2, LocalDate.of(2026, 7, 2), "Evening round",
      List(marko, anna), Csv.read(data / "abendrunde.csv").get))
    repo.addTrip(Trip(3, 1, LocalDate.of(2026, 8, 9), "Meersburg – Konstanz",
      List(anna), Gpx.read(data / "konstanz-meersburg.gpx").get.reverse))
