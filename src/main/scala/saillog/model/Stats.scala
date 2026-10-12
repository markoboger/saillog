package saillog.model

import java.time.Duration

object Stats:
  def nmPerBoat(trips: List[Trip]): Map[Int, Double] =
    trips.groupMapReduce(_.boatId)(t => Geo.distanceNm(t.track))(_ + _)

  def nmPerSeason(trips: List[Trip]): Map[Int, Double] =
    trips.groupMapReduce(_.date.getYear)(t => Geo.distanceNm(t.track))(_ + _)

  def hours(t: Trip): Double =
    Duration.between(t.track.head.time, t.track.last.time).toSeconds / 3600.0

  def avgSpeedKn(t: Trip): Double = Geo.distanceNm(t.track) / hours(t)
