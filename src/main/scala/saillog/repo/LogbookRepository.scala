package saillog.repo

import saillog.model.*

trait LogbookRepository:
  def boats: List[Boat]
  def addBoat(boat: Boat): Unit
  def trips: List[Trip]
  def addTrip(trip: Trip): Unit

  def trip(id: Int): Option[Trip] = trips.find(_.id == id)
  def boat(id: Int): Option[Boat] = boats.find(_.id == id)
  def nextTripId: Int = trips.map(_.id).maxOption.getOrElse(0) + 1

  def nmPerBoat: Map[String, Double] =
    Stats.nmPerBoat(trips).map((id, nm) => boat(id).fold("?")(_.name) -> nm)
