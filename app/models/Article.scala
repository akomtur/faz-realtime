package models


case class Article(id: String, title: String, published: Long, teaser : Option[String], url : Option[String], image: Option[String])
