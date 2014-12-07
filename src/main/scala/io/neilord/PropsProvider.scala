package io.neilord

import akka.actor.Props
import io.neilord.serial.SerialPortSubscriptionManager
import io.neilord.serial.wrapper.SerialPort

trait PropsProvider {
  def getProps(port: SerialPort): Props
}

trait RealPropsProvider extends PropsProvider {
  override def getProps(port: SerialPort) = {
    Props(classOf[SerialPortSubscriptionManager], port)
  }
}