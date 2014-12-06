package io.neilord.serial.models

import akka.actor.ActorRef

object Messages {

  trait Command

  trait Notification

  case class OpenPort(settings: SerialPortConfig) extends Command

  case class PortOpened(settings: SerialPortConfig, handler: ActorRef) extends Notification

  case class ClosePort() extends Command

  case class PortClosed() extends Notification

  case class CommandFailed(command: Command, cause: Throwable)

  case class Register(actor: ActorRef) extends Command

  case class Unregister(actor: ActorRef) extends Command

  case class LineReceived(content: String) extends Notification

}
