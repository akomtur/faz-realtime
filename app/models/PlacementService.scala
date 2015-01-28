package models

import play.api.{Play, Application}
import play.api.libs.json.{JsSuccess, JsValue}
import play.api.libs.ws.WS

import scala.concurrent.Future


object PlacementService extends ArticleJsonConverter {
  import scala.concurrent.ExecutionContext.Implicits.global

  lazy val endpoint = Play.current.configuration.getString("article.json.endpoint") getOrElse "http://www.faz.net/json/"

  def getArticles(id: String)(implicit app: Application) : Future[Seq[Article]] = {
    WS.url(s"$endpoint$id/")
      .withHeaders("Accept" -> "application/v2.8+json")
      .get()
      .map({ response =>
      val entries : Seq[JsValue] = (response.json \ "content").as[Seq[JsValue]];
      for (
        entry: JsValue <- entries;
        article : Article <- entry.asOpt[Article])
        yield article;
    })
  }
}
