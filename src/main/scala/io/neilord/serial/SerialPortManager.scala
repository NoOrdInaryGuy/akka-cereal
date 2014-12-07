package io.neilord.serial

import akka.actor.{Props, ActorLogging, Actor}
import io.neilord.serial.factory.SerialPortFactory
import io.neilord.serial.models.Messages
import Messages.OpenPort
import Messages.PortOpened
import scala.util.Try
import Messages.CommandFailed
import io.neilord.serial.wrapper.SerialPort
import io.neilord.PropsProvider

class SerialPortManager(portFactory: SerialPortFactory)
  extends Actor with ActorLogging {
  //Want to be able to inject this for testing
  this: PropsProvider =>

  override def receive = {
    case command @ OpenPort(serialConfig) =>
        val port = portFactory.newInstance(serialConfig.portName)
        Try {
          port.open()
          port.setParameters(serialConfig)
          val handler = context.actorOf(
            //TODO
            //SerialPortSubscriptionManager.props(port)
            //Props(classOf[SerialPortSubscriptionManager], port), escapePort(port.name)
            getProps(port)
          )
          sender ! PortOpened(handler)
        } recover {
          case exc: Throwable => sender ! CommandFailed(command, exc)
        }
    }
}

object SerialPortManager {
  def escapePort(portName: String): String = portName collect {
    case '/' => '-'
    case otherChar => otherChar
  }
}
