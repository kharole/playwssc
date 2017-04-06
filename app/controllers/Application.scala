package controllers

import javax.inject.Inject

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import play.api._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.api.Logger

class Application @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def ws: WebSocket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(out))
  }

  object MyWebSocketActor {
    def props(out: ActorRef) = Props(new MyWebSocketActor(out))
  }

  class MyWebSocketActor(out: ActorRef) extends Actor {

    override def preStart(): Unit = {
      Logger.info("actor started")
    }

    override def postStop(): Unit = {
      Logger.info("actor stopped")
    }

    def receive = {
      case msg: String =>
        out ! ("I received your message: " + msg)
    }
  }

}