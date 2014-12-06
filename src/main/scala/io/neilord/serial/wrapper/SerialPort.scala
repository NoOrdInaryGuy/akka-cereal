package io.neilord.serial.wrapper

import io.neilord.serial.models.SerialPortConfig

trait SerialPort {
  def open(): Boolean

  def close()

  def setParameters(serialPortParams: SerialPortConfig)

  def name: String

  def isOpen: Boolean

  def readBytes(): Array[Byte]
}
