package models

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


trait ArticleJsonConverter {

  implicit val articleReads : Reads[Article] = (
      (JsPath \ "ID").read[String] and
      (JsPath \ "Ueberschrift").read[String] and
      (JsPath \ "Veroeffentlichungsdatum").read[Long] and
      (JsPath \ "Teasertext").readNullable[String] and
      (JsPath \ "URL").readNullable[String] and
      (JsPath \ "Aufmacherbild" \ "URL").readNullable[String])(Article.apply _)

  implicit val articleWrites : Writes[Article] = (
      (JsPath \ "id").write[String] and
      (JsPath \ "title").write[String] and
      (JsPath \ "published").write[Long] and
      (JsPath \ "teaser").writeNullable[String] and
      (JsPath \ "url").writeNullable[String] and
      (JsPath \ "imageUrl").writeNullable[String]
    )(unlift(Article.unapply))
}
