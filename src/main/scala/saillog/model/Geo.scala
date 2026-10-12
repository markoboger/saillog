package saillog.model

object Geo:
  val EarthRadiusNm = 3440.065

  def haversineNm(a: TrackPoint, b: TrackPoint): Double =
    val dLat = math.toRadians(b.lat - a.lat)
    val dLon = math.toRadians(b.lon - a.lon)
    val h = math.pow(math.sin(dLat / 2), 2) +
      math.cos(math.toRadians(a.lat)) * math.cos(math.toRadians(b.lat)) *
      math.pow(math.sin(dLon / 2), 2)
    2 * EarthRadiusNm * math.asin(math.sqrt(h))

  def distanceNm(track: List[TrackPoint]): Double =
    track.zip(track.drop(1)).map(haversineNm).sum
