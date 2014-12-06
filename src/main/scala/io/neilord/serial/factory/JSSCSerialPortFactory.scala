package io.neilord.serial.factory

import io.neilord.serial.wrapper.{JSSCSerialPortWrapper, SerialPort}

class JSSCSerialPortFactory extends SerialPortFactory {
  override def newInstance(name: String): SerialPort = {
    val unwrapped = new jssc.SerialPort(name)
    unwrapped.setEventsMask(jssc.SerialPort.MASK_RXCHAR)
    new JSSCSerialPortWrapper(unwrapped)
  }
}
