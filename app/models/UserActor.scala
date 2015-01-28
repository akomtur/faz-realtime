package models

import akka.actor.{Props, ActorRef, Actor}
import play.api.libs.json.Json


object UserActor {
  def props(department: String, departments : ActorRef, out : ActorRef) : Props = Props(new UserActor(department, departments, out))
}

class UserActor(department: String, departments : ActorRef, out: ActorRef) extends Actor with ArticleJsonConverter {


  override def preStart(): Unit = {
    departments ! AddListener(department, self)
  }

  override def receive: Receive = {

    case UpdateMessage(article) => {
      out ! Json.toJson(article).toString()
    }
  }
}
