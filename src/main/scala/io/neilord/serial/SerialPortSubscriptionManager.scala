package io.neilord.serial

import akka.actor.{Props, ActorRef, Actor, ActorLogging}
import io.neilord.serial.models.Messages
import Messages._
import scala.util.Try
import scala.collection.mutable
import scala.util.Success
import scala.util.Failure
import Messages.ClosePort
import Messages.CommandFailed
import Messages.PortClosed
import io.neilord.serial.wrapper.SerialPort

class SerialPortSubscriptionManager(port: SerialPort) extends Actor with ActorLogging {

  private val subscribers = mutable.HashSet[ActorRef]()
  val lineReader = context.system.actorOf(Props(classOf[LineReader], port, self), s"line-reader-${port.name}")

  private def tellAllSubscribers(msg: Any) = {
    subscribers.foreach { subscriber =>
      subscriber ! msg
    }
  }

  override def receive = {
    case c @ ClosePort() => {
      tellAllSubscribers(PortClosed())
      context stop self
      Try {
        port.close()
      } match {
        case Failure(t) => sender ! CommandFailed(c, t)
        case Success(_) => sender ! PortClosed()
      }
    }
    case Register(actor) =>  subscribers += actor
    case Unregister(actor) => subscribers -= actor
    case msg: LineReceived => tellAllSubscribers(msg)
  }

  override def postStop() {
    if (port.isOpen) port.close()
  }

}
