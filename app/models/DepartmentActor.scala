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
    case UpdateMessage(message) => {
      context.children map { child =>
        child.forward(UpdateMessage(message))
      }
    }
    case Terminated(children) => if(context.children.size == 0) self ! PoisonPill

    case UpdateState => {
      PlacementService.getArticles(name) map { articles : Seq[Article] =>
        articles map { article => {
          if(!articleIds.exists(_ equals article.id)) {
            articleIds = articleIds + article.id
            self ! UpdateMessage(article)
          }
        }}
      }
    }

  }
}

class DepartmentListener(user : ActorRef) extends Actor {
  context watch user

  override def receive: Actor.Receive = {
    case UpdateMessage(message) => user ! UpdateMessage(message)
    case Terminated(ref) => self ! PoisonPill
  }
}
