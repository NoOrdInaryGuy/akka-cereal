package io.neilord.serial

import akka.actor.{ActorLogging, ActorRef, Actor}
import io.neilord.serial.wrapper.SerialPort
import io.neilord.serial.models.Messages.{DataAvailable, BytesReceived}

class BytesReader(serialPort: SerialPort, bytesSubscriber: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case DataAvailable => {
      val bytes = serialPort.readBytes()
      log.debug(s"Read data: ${new String(bytes.toArray)}")
      bytesSubscriber ! BytesReceived(bytes)
    }
  }
}
