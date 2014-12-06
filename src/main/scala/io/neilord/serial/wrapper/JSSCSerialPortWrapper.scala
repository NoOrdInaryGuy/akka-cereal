package io.neilord.serial.wrapper

import io.neilord.serial.models.SerialPortConfig
import scala.util.Try

class JSSCSerialPortWrapper(serialPort: jssc.SerialPort) extends SerialPort {
  override def setParameters(serialPortParams: SerialPortConfig) {
    Try {
      serialPort.setParams(
        serialPortParams.baudRate,
        serialPortParams.dataBits,
        serialPortParams.stopBits,
        serialPortParams.parity
      )
    }
  }

  override def close(): Unit = serialPort.closePort()

  override def open(): Boolean = serialPort.openPort()

  override def name: String = serialPort.getPortName

  override def isOpen: Boolean = serialPort.isOpened

  override def readBytes(): Array[Byte] = serialPort.readBytes()
}
