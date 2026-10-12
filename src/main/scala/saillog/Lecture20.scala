package saillog

import saillog.repo.*

def report(repo: LogbookRepository): Unit =
  println(s"  boats: ${repo.boats.map(_.name)}")
  println(s"  trips: ${repo.trips.map(t => s"${t.id} ${t.title} (${t.track.size} pts)")}")
  println(s"  nm per boat: ${repo.nmPerBoat}")
  println(s"  trip 2 crew: ${repo.trip(2).map(_.crew.map(_.name))}")

@main def lecture20(): Unit =
  val data = os.pwd / "data"
  os.remove(os.pwd / "logbook.json")
  os.remove(os.pwd / "logbook.db")
  val repos = List(
    "JSON file" -> JsonFileRepository(os.pwd / "logbook.json"),
    "SQLite"    -> SqlRepository("jdbc:sqlite:logbook.db"))
  for (name, repo) <- repos do
    SampleData.fill(repo, data)
    println(name)
    report(repo)
  val sql = SqlRepository("jdbc:sqlite:logbook.db")
  println(s"SQL nm per season: ${sql.nmPerSeason}")
  println(s"same trips in both: ${repos(0)._2.trips == repos(1)._2.trips}")

@main def showSql(): Unit =
  import scalasql.*, scalasql.SqliteDialect.*
  val ds = org.sqlite.SQLiteDataSource(); ds.setUrl("jdbc:sqlite::memory:")
  val client = DbClient.DataSource(ds, config = new Config {})
  client.transaction { db =>
    println(db.renderSql(
      TripRow.select.join(BoatRow)(_.boatId === _.id)
        .groupBy { case (_, boat) => boat.name } { rows => rows.sumBy(_._1.distanceNm) }))
  }
