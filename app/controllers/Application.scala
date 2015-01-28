package controllers

import akka.actor.Props
import models._
import play.api.mvc._
import play.libs.Akka

object Application extends Controller {

  import scala.concurrent.ExecutionContext.Implicits.global

  val departments = Akka.system().actorOf(Props(classOf[DepartmentsActor]), "departments")

  implicit val application = play.api.Play.current

  def showDepartment(department: String) = Action.async {
    PlacementService.getArticles(department).map { articles : Seq[Article] =>
      Ok(views.html.department(department, articles))
    }
  }

  def sendMessage(department : String, message : String) = Action {
    departments ! SendMessage(department, message)
    Ok(s"message '$message' sent to '$department'")
  }

  def websocketForChannel(channelName : String) = WebSocket.acceptWithActor[String, String] { request => out =>
    UserActor.props(channelName, departments, out)
  }

  def index = Action {
    Redirect(routes.Application.showDepartment("aktuell"))
  }
}