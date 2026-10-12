package saillog.view

import scalatags.Text.all.*
import scalatags.Text.tags2.{nav, main as mainTag, section, article}

object Layout:
  def page(pageTitle: String)(content: Frag*): String =
    "<!doctype html>" + html(lang := "en")(
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", attr("content") := "width=device-width, initial-scale=1"),
        tag("title")(pageTitle),
        link(rel := "stylesheet", href := "/static/style.css")),
      body(
        header(
          h1("Sail Log"),
          nav(a(href := "/", "Trips"), a(href := "/trips/new", "New trip"),
              a(href := "/stats", "Statistics"))),
        mainTag(content))
    ).render
