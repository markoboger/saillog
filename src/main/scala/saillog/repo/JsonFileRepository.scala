package saillog.repo

import upickle.default.*
import saillog.model.{*, given}

case class Logbook(boats: List[Boat], trips: List[Trip]) derives ReadWriter

class JsonFileRepository(file: os.Path) extends LogbookRepository:
  private def load: Logbook =
    if os.exists(file) then read[Logbook](os.read(file)) else Logbook(Nil, Nil)
  private def save(lb: Logbook): Unit = os.write.over(file, write(lb, indent = 2))

  def boats: List[Boat] = load.boats
  def trips: List[Trip] = load.trips
  def addBoat(boat: Boat): Unit = save(load.copy(boats = load.boats :+ boat))
  def addTrip(trip: Trip): Unit = save(load.copy(trips = load.trips :+ trip))
