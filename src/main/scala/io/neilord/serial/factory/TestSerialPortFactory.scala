package io.neilord.serial.factory

import io.neilord.serial.wrapper.{TestSerialPort, SerialPort}

class TestSerialPortFactory extends SerialPortFactory {
  override def newInstance(name: String): SerialPort = new TestSerialPort(name)
}
