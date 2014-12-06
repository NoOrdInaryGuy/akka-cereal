package io.neilord.serial.factory

import io.neilord.serial.wrapper.SerialPort

trait SerialPortFactory {
  def newInstance(name: String): SerialPort
}
