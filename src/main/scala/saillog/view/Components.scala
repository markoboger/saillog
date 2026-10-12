package saillog.view

import scalatags.Text.all.*
import scalatags.Text.svgTags.{svg, polyline}
import scalatags.Text.svgAttrs.{viewBox, points, fill, strokeWidth}
import scalatags.Text.tags2.{section, article}
import saillog.model.*

object Components:
  def tripCard(t: Trip, boat: String, longest: Boolean): Frag =
    article(cls := (if longest then "card card--longest" else "card"))(
      h3(a(href := s"/trips/${t.id}", t.title)),
      p(s"${t.date} · $boat"),
      p(cls := "nm", f"${Geo.distanceNm(t.track)}%.1f nm"))

  def crewList(crew: List[CrewMember]): Frag =
    ul(for c <- crew yield li(s"${c.name} (${c.role})"))

  def pointsTable(track: List[TrackPoint]): Frag =
    table(
      thead(tr(th("Time"), th("Lat"), th("Lon"))),
      tbody(for p <- track yield tr(td(p.time.toString), td(p.lat), td(p.lon))))

  def trackSvg(track: List[TrackPoint], w: Int = 400, h: Int = 300): Frag =
    val lats = track.map(_.lat); val lons = track.map(_.lon)
    val scale = math.min(w / (lons.max - lons.min max 1e-9),
                         h / (lats.max - lats.min max 1e-9))
    val pts = track.map { p =>
      f"${(p.lon - lons.min) * scale}%.1f,${h - (p.lat - lats.min) * scale}%.1f"
    }.mkString(" ")
    svg(viewBox := s"0 0 $w $h", width := w, height := h)(
      polyline(points := pts, fill := "none", strokeWidth := 3))

  def bar(label: String, nm: Double, max: Double): Frag =
    div(cls := "bar-row")(
      span(cls := "bar-label", label),
      div(cls := "bar", width := s"${(nm / max * 70).round}%",
          backgroundColor := "var(--sea)"),
      span(f"$nm%.1f nm"))
