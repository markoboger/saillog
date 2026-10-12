package saillog.repo

import scalasql.*

case class BoatRow[T[_]](id: T[Int], name: T[String], kind: T[String])
object BoatRow extends Table[BoatRow]

case class TripRow[T[_]](id: T[Int], boatId: T[Int], date: T[String],
                         title: T[String], distanceNm: T[Double])
object TripRow extends Table[TripRow]

case class PointRow[T[_]](tripId: T[Int], idx: T[Int], lat: T[Double],
                          lon: T[Double], time: T[String])
object PointRow extends Table[PointRow]

case class CrewRow[T[_]](tripId: T[Int], name: T[String], role: T[String])
object CrewRow extends Table[CrewRow]

object Schema:
  val ddl = List(
    "CREATE TABLE IF NOT EXISTS boat_row (id INTEGER PRIMARY KEY, name TEXT, kind TEXT)",
    "CREATE TABLE IF NOT EXISTS trip_row (id INTEGER PRIMARY KEY, boat_id INTEGER, " +
      "date TEXT, title TEXT, distance_nm REAL)",
    "CREATE TABLE IF NOT EXISTS point_row (trip_id INTEGER, idx INTEGER, " +
      "lat REAL, lon REAL, time TEXT)",
    "CREATE TABLE IF NOT EXISTS crew_row (trip_id INTEGER, name TEXT, role TEXT)")
