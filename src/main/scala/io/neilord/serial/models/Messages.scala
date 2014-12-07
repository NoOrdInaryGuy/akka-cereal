package io.neilord.serial.models

import akka.actor.ActorRef
import scala.collection.immutable.Seq

object Messages {

  trait Command

  trait Notification

  case class OpenPort(settings: SerialPortConfig) extends Command

  case class PortOpened(subscriptionManager: ActorRef) extends Notification

  case class ClosePort() extends Command

  case class PortClosed() extends Notification

  case class CommandFailed(command: Command, cause: Throwable)

  case class RegisterForBytes(actor: ActorRef) extends Command

  case class Unregister(actor: ActorRef) extends Command

  case class LineReceived(content: String) extends Notification

  case class BytesReceived(content: Seq[Byte]) extends Notification

  case object DataAvailable extends Notification

}
