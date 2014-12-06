package io.neilord.serial.wrapper

import io.neilord.serial.models.SerialPortConfig
import scala.util.Try
import akka.actor.ActorRef
import jssc.{SerialPortEvent, SerialPortEventListener}
import scala.collection.immutable.Seq
import io.neilord.serial.models.Messages.DataAvailable

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

  override def readBytes(): Seq[Byte] = serialPort.readBytes().toIndexedSeq

  override def subscribe(handler: ActorRef) = {
    val adapter = new JSSCSerialPortEventToAkkaAdapter(handler)
    serialPort.addEventListener(adapter)
  }
}

class JSSCSerialPortEventToAkkaAdapter(targetActor: ActorRef) extends SerialPortEventListener {
  def serialEvent(event: SerialPortEvent) {
    if(event.isRXCHAR) {
      targetActor ! DataAvailable
    }
  }
}
