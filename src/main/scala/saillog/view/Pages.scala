package saillog.view

import scalatags.Text.all.*
import scalatags.Text.tags2.{section, article}
import saillog.model.*
import Components.*

object Pages:
  def overview(trips: List[(Trip, String)]): String =
    val maxNm = trips.map((t, _) => Geo.distanceNm(t.track)).maxOption.getOrElse(0.0)
    Layout.page("Trips")(
      h2("Trips"),
      section(cls := "cards")(
        for (t, boat) <- trips
        yield tripCard(t, boat, Geo.distanceNm(t.track) == maxNm)))

  def detail(t: Trip, boat: String): String =
    Layout.page(t.title)(
      article(
        h2(t.title),
        p(f"${t.date} · $boat · ${Geo.distanceNm(t.track)}%.1f nm · ${Stats.avgSpeedKn(t)}%.1f kn"),
        div(cls := "detail")(
          section(h3("Track"), trackSvg(t.track)),
          section(h3("Crew"), crewList(t.crew))),
        section(h3("Track points"), pointsTable(t.track))))

  def form(boats: List[Boat]): String =
    Layout.page("New trip")(
      h2("New trip"),
      scalatags.Text.all.form(method := "post", action := "/trips",
                              enctype := "multipart/form-data")(
        label(`for` := "title", "Title"), input(id := "title", name := "title", required),
        label(`for` := "date", "Date"),
        input(id := "date", name := "date", `type` := "date", required),
        label(`for` := "boat", "Boat"),
        select(id := "boat", name := "boat")(
          for b <- boats yield option(value := b.id, b.name)),
        label(`for` := "track", "Track (GPX or CSV)"),
        input(id := "track", name := "track", `type` := "file", accept := ".gpx,.csv", required),
        button(`type` := "submit", "Save")))

  def stats(perSeason: Map[Int, Double], perBoat: Map[String, Double]): String =
    val max = (perSeason.values ++ perBoat.values).maxOption.getOrElse(1.0)
    Layout.page("Statistics")(
      h2("Nautical miles per season"),
      for (season, nm) <- perSeason.toList.sorted yield bar(season.toString, nm, max),
      h2("Nautical miles per boat"),
      for (boat, nm) <- perBoat.toList.sorted yield bar(boat, nm, max))

  def notFound(what: String): String = Layout.page("Not found")(p(s"No $what"))
