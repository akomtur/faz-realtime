package models

import java.util.Date

import akka.actor.{ActorRef, Props, Actor}
import StringConverter._
import scala.concurrent.duration.DurationLong


case class AddListener(department : String, listener: ActorRef)
case class SendMessage(department : String, message : String)


class DepartmentsActor extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global


  override def receive: Receive = {
    case AddListener(department, listener) => {
      val departmentRef = context.child(department sanitize) match {
        case Some(ref) => ref
        case None => createDepartmentActor(department)
      }
      departmentRef ! AddChildren(listener)
    }
    case SendMessage(dep, message) => {
      context.child(dep sanitize) map {ref : ActorRef => {
        ref ! UpdateMessage(Article(Math.random().toString, message, new Date().getTime, Some("teaser"), Some("http://localhost"), None))
      }}
    }
  }

  private def createDepartmentActor(department: String) : ActorRef = {
    val newDepartment = context.actorOf(Props(new DepartmentActor(department)), department sanitize)
    context.system.scheduler.schedule(0 minutes, 1 minute, newDepartment, UpdateState)
    newDepartment ! UpdateState
    newDepartment

  }
}
