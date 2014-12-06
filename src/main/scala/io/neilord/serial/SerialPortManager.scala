package io.neilord.serial

import akka.actor.{Props, ActorLogging, Actor}
import io.neilord.serial.factory.SerialPortFactory
import io.neilord.serial.models.Messages
import Messages.OpenPort
import Messages.PortOpened
import scala.util.Try
import Messages.CommandFailed

class SerialPortManager(portFactory: SerialPortFactory) extends Actor with ActorLogging {

  import SerialPortManager._

  override def receive = {
    case command @ OpenPort(serialConfig) =>
        val port = portFactory.newInstance(serialConfig.name)
        Try {
          port.open()
          port.setParameters(serialConfig)
          val handler = context.system.actorOf(
            Props(classOf[SerialPortSubscriptionManager], port),
            name = escapePort(port.name)
          )
          sender ! PortOpened(serialConfig, handler)
        } recover {
          case exc: Throwable => {
            sender ! CommandFailed(command, exc)
          }
        }
    }
}

object SerialPortManager {
  def escapePort(portName: String): String = portName collect {
    case '/' => '-'
    case otherChar => otherChar
  }
}
