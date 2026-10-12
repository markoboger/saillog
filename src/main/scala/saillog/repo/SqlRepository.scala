package saillog.repo

import java.time.{Instant, LocalDate}
import scalasql.*, scalasql.SqliteDialect.*
import saillog.model.*

class SqlRepository(url: String) extends LogbookRepository:
  private val ds = org.sqlite.SQLiteDataSource()
  ds.setUrl(url)
  private val client = DbClient.DataSource(ds, config = new Config {})
  client.transaction(db => Schema.ddl.foreach(db.updateRaw(_)))

  def boats: List[Boat] = client.transaction { db =>
    db.run(BoatRow.select).map(r => Boat(r.id, r.name, BoatKind.valueOf(r.kind))).toList
  }

  def addBoat(b: Boat): Unit = client.transaction { db =>
    db.run(BoatRow.insert.columns(_.id := b.id, _.name := b.name, _.kind := b.kind.toString))
  }

  def trips: List[Trip] = client.transaction { db =>
    db.run(TripRow.select.sortBy(_.id)).map { r =>
      val crew = db.run(CrewRow.select.filter(_.tripId === r.id))
        .map(c => CrewMember(c.name, Role.valueOf(c.role)))
      val track = db.run(PointRow.select.filter(_.tripId === r.id).sortBy(_.idx))
        .map(p => TrackPoint(p.lat, p.lon, Instant.parse(p.time)))
      Trip(r.id, r.boatId, LocalDate.parse(r.date), r.title, crew.toList, track.toList)
    }.toList
  }

  def addTrip(t: Trip): Unit = client.transaction { db =>
    db.run(TripRow.insert.columns(_.id := t.id, _.boatId := t.boatId,
      _.date := t.date.toString, _.title := t.title, _.distanceNm := Geo.distanceNm(t.track)))
    for c <- t.crew do
      db.run(CrewRow.insert.columns(_.tripId := t.id, _.name := c.name, _.role := c.role.toString))
    for (p, i) <- t.track.zipWithIndex do
      db.run(PointRow.insert.columns(_.tripId := t.id, _.idx := i,
        _.lat := p.lat, _.lon := p.lon, _.time := p.time.toString))
  }

  override def nmPerBoat: Map[String, Double] = client.transaction { db =>
    db.run(
      TripRow.select
        .join(BoatRow)(_.boatId === _.id)
        .groupBy { case (_, boat) => boat.name } { rows => rows.sumBy(_._1.distanceNm) }
    ).toMap
  }

  def nmPerSeason: Map[String, Double] = client.transaction { db =>
    db.run(TripRow.select.groupBy(_.date.substring(1, 4))(_.sumBy(_.distanceNm))).toMap
  }
