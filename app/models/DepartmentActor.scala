package models

import akka.actor._

import scala.concurrent.duration.DurationInt

case class AddChildren(child : ActorRef)
case class UpdateMessage(article : Article)
case class UpdateState()

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class DepartmentActor(name: String) extends Actor {

  /**
   * A mutable sequence of all articles that this department contains. Its only for the internal
   * state when a new user listens to this department. This Sequence will always cleaned when a new
   * update starts.
   */
  var articles : Seq[Article] = Nil

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = self ! UpdateState

  override def receive: Receive = {
    case AddChildren(user) => {
      val newChildren = context.actorOf(Props(classOf[DepartmentListener], user))
      articles.foreach(newChildren ! UpdateMessage(_))
    }

    case msg : UpdateMessage => {
      articles ++= Seq(msg.article)
      context.children.foreach(_ ! msg)
    }

    case Terminated(children) => if(context.children.isEmpty) self ! PoisonPill

    case UpdateState => {
      updateArticlesInDepartment
      context.system.scheduler.scheduleOnce(1 minute, self, UpdateState)
    }
  }

  private def updateArticlesInDepartment {
    articles = Nil
    for (
      articles <- PlacementService.getArticles(name);
      article <- articles
    ) self ! UpdateMessage(article)
  }

}

class DepartmentListener(user : ActorRef) extends Actor {
  context watch user

  /**
   * A Set of article ids that is needed to send only new articles to the client. It represents
   * the current state for an user which articles already sent.
   */
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
