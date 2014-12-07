package io.neilord

import akka.actor.Props
import io.neilord.serial.wrapper.SerialPort
import scala.reflect._

trait PropsProvider {
  def getProps(port: SerialPort): Props
}

trait RealPropsProvider extends PropsProvider {
  val ctag: ClassTag[_]

  override def getProps(port: SerialPort) = {
    Props(ctag.runtimeClass, port)
  }
}