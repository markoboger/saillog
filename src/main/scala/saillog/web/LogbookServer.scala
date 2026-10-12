package saillog.web

import java.time.LocalDate
import upickle.default.*
import saillog.io.*
import saillog.model.{*, given}
import saillog.repo.*

object LogbookServer extends cask.MainRoutes:
  override def host = "0.0.0.0"
  override def port = 8080

  val repo: LogbookRepository = SqlRepository("jdbc:sqlite:logbook.db")
  if repo.boats.isEmpty then SampleData.fill(repo, os.pwd / "data")

  def json[T: Writer](value: T, status: Int = 200) =
    cask.Response(writeJs(value), statusCode = status)

  @cask.get("/api/boats")
  def boats() = json(repo.boats)

  @cask.get("/api/trips")
  def trips(season: Option[Int] = None) =
    json(repo.trips.filter(t => season.forall(_ == t.date.getYear)))

  @cask.get("/api/trips/:id")
  def trip(id: Int) = repo.trip(id) match
    case Some(t) => json(t)
    case None    => json(ujson.Obj("error" -> s"no trip $id"), 404)

  @cask.post("/api/trips")
  def upload(boat: Int, title: String, date: String, format: String = "gpx",
             request: cask.Request) =
    val text = request.text()
    val track = if format == "csv" then Csv.parse(text.linesIterator.toSeq)
                else Gpx.parse(text)
    val trip = Trip(repo.nextTripId, boat, LocalDate.parse(date), title, Nil, track)
    repo.addTrip(trip)
    json(ujson.Obj("id" -> trip.id, "points" -> track.size,
                   "nm" -> Geo.distanceNm(track)), 201)

  @cask.get("/api/stats/nm-per-boat")
  def nmPerBoat() = json(repo.nmPerBoat)

  // ---- Lecture 22: HTML pages ----

  def html(content: String, status: Int = 200) =
    cask.Response(content, statusCode = status,
                  headers = Seq("Content-Type" -> "text/html; charset=utf-8"))

  def boatName(id: Int): String = repo.boat(id).fold("?")(_.name)

  @cask.get("/")
  def index() = html(RawHtml.overview(repo.trips.map(t => (t, boatName(t.boatId)))))

  @cask.get("/trips/:id")
  def tripPage(id: Int) = repo.trip(id) match
    case Some(t) => html(RawHtml.detail(t, boatName(t.boatId)))
    case None    => html(RawHtml.page("Not found", s"<p>No trip $id</p>"), 404)

  @cask.get("/trips/new")
  def newTrip() = html(RawHtml.form(repo.boats))

  @cask.postForm("/trips")
  def createTrip(title: String, date: String, boat: Int, track: cask.FormFile) =
    val file = os.Path(track.filePath.get)
    val text = os.read(file)
    val points = if track.fileName.endsWith(".csv") then Csv.parse(text.linesIterator.toSeq)
                 else Gpx.parse(text)
    val trip = Trip(repo.nextTripId, boat, LocalDate.parse(date), title, Nil, points)
    repo.addTrip(trip)
    cask.Redirect(s"/trips/${trip.id}")

  initialize()
