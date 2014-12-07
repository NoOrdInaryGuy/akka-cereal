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

  val escapedName = SerialPortManager.escapePort(port.name)
  val bytesReader = context.system.actorOf(Props(classOf[BytesReader], port, self), s"bytes-reader-$escapedName")
  port.subscribe(bytesReader)

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
    case RegisterForBytes(actorRef) =>
      log.info(s"Adding actor $actorRef to subscribers")
      subscribers += actorRef
    case Unregister(actorRef) =>
      log.info(s"Removing actor $actorRef to subscribers")
      subscribers -= actorRef
    case msg: BytesReceived => {
      tellAllSubscribers(msg)
    }
  }

  override def postStop() {
    if (port.isOpen) port.close()
  }

}

object SerialPortSubscriptionManager {
  def props(port: SerialPort): Props = {
    Props(new SerialPortSubscriptionManager(port))
  }
}