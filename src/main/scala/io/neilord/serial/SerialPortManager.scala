package io.neilord.serial

import akka.actor.{Props, ActorLogging, Actor}
import io.neilord.serial.factory.SerialPortFactory
import io.neilord.serial.models.Messages
import Messages.OpenPort
import Messages.PortOpened
import scala.util.Try
import Messages.CommandFailed
import io.neilord.serial.wrapper.SerialPort
import io.neilord.{RealPropsProvider, PropsProvider}
import scala.reflect.ClassTag

class SerialPortManager[ChildActor : ClassTag](portFactory: SerialPortFactory)
  extends Actor with ActorLogging {
  //Need to be able to inject this for testing
  this: PropsProvider =>

  val ctag = implicitly[ClassTag[ChildActor]]

  override def receive = {
    case command @ OpenPort(serialConfig) =>
        val port = portFactory.newInstance(serialConfig.portName)
        Try {
          port.open()
          port.setParameters(serialConfig)
          val handler = context.actorOf(getProps(port))
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

  def apply(serialPortFactory: SerialPortFactory) = {
    new SerialPortManager[SerialPortSubscriptionManager](serialPortFactory) with RealPropsProvider
  }
}