package saillog.web

import saillog.model.*

/** Lecture 22: HTML built from strings. Works – but see the problems. */
object RawHtml:
  def page(title: String, body: String): String =
    s"""<!doctype html>
       |<html lang="en">
       |<head><meta charset="utf-8"><title>$title</title></head>
       |<body>
       |<header><h1>Sail Log</h1>
       |  <nav><a href="/">Trips</a> | <a href="/trips/new">New trip</a></nav>
       |</header>
       |<main>$body</main>
       |</body>
       |</html>""".stripMargin

  def tripRow(t: Trip, boat: String): String =
    f"""<tr><td>${t.date}</td><td><a href="/trips/${t.id}">${t.title}</a></td>
       |<td>$boat</td><td>${Geo.distanceNm(t.track)}%.1f</td></tr>""".stripMargin

  def overview(rows: List[(Trip, String)]): String =
    page("Trips", s"""<h2>Trips</h2>
      |<table>
      |  <thead><tr><th>Date</th><th>Trip</th><th>Boat</th><th>nm</th></tr></thead>
      |  <tbody>${rows.map(tripRow).mkString("\n")}</tbody>
      |</table>""".stripMargin)

  def trackSvg(track: List[TrackPoint], w: Int = 400, h: Int = 300): String =
    val lats = track.map(_.lat); val lons = track.map(_.lon)
    val scale = math.min(w / (lons.max - lons.min max 1e-9),
                         h / (lats.max - lats.min max 1e-9))
    val points = track.map { p =>
      f"${(p.lon - lons.min) * scale}%.1f,${h - (p.lat - lats.min) * scale}%.1f"
    }.mkString(" ")
    s"""<svg viewBox="0 0 $w $h" width="$w" height="$h">
       |  <polyline points="$points" fill="none" stroke="navy" stroke-width="3"/>
       |</svg>""".stripMargin

  def detail(t: Trip, boat: String): String =
    val crew = t.crew.map(c => s"<li>${c.name} (${c.role})</li>").mkString
    val points = t.track.map(p => s"<tr><td>${p.time}</td><td>${p.lat}</td><td>${p.lon}</td></tr>").mkString
    page(t.title, f"""<article>
      |<h2>${t.title}</h2>
      |<p>${t.date} · $boat · ${Geo.distanceNm(t.track)}%.1f nm · ${Stats.avgSpeedKn(t)}%.1f kn</p>
      |<section><h3>Track</h3>${trackSvg(t.track)}</section>
      |<section><h3>Crew</h3><ul>$crew</ul></section>
      |<section><h3>Track points</h3>
      |<table><thead><tr><th>Time</th><th>Lat</th><th>Lon</th></tr></thead>
      |<tbody>$points</tbody></table></section>
      |</article>""".stripMargin)

  def form(boats: List[Boat]): String =
    val options = boats.map(b => s"""<option value="${b.id}">${b.name}</option>""").mkString
    page("New trip", s"""<h2>New trip</h2>
      |<form method="post" action="/trips" enctype="multipart/form-data">
      |  <label for="title">Title</label>
      |  <input id="title" name="title" required>
      |  <label for="date">Date</label>
      |  <input id="date" name="date" type="date" required>
      |  <label for="boat">Boat</label>
      |  <select id="boat" name="boat">$options</select>
      |  <label for="track">Track (GPX or CSV)</label>
      |  <input id="track" name="track" type="file" accept=".gpx,.csv" required>
      |  <button type="submit">Save</button>
      |</form>""".stripMargin)
