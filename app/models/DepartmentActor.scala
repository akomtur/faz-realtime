package models

import akka.actor._

case class AddChildren(child : ActorRef)
case class UpdateMessage(article : Article)

case class UpdateState()

class DepartmentActor(name: String) extends Actor {
  import play.api.Play.current
  import scala.concurrent.ExecutionContext.Implicits.global

  var articleIds : Set[String] = Set.empty

  override def receive: Receive = {
    case AddChildren(children) => context.actorOf(Props(classOf[DepartmentListener], children))
    case msg : UpdateMessage => {
      context.children.foreach(_ ! msg)
    }
    case Terminated(children) => if(context.children.isEmpty) self ! PoisonPill

    case UpdateState => {
      for(
        articles <- PlacementService.getArticles(name);
        article <- articles
      ) self ! UpdateMessage(article)
    }

  }
}

class DepartmentListener(user : ActorRef) extends Actor {
  context watch user

  var articleIds : Set[String] = Set.empty

  override def receive: Actor.Receive = {
    case UpdateMessage(article) => {
      if(!articleIds.contains(article.id)) {
        articleIds = articleIds + article.id
        user ! UpdateMessage(article)
      }
    }
    case Terminated(ref) => self ! PoisonPill
  }
}
