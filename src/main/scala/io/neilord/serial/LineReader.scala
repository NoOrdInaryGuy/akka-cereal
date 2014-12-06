package io.neilord.serial

import akka.actor.{ActorRef, Actor}
import jssc.SerialPortEvent
import java.io.{PipedInputStream, InputStreamReader, BufferedReader, PipedOutputStream}
import io.neilord.serial.wrapper.SerialPort
import io.neilord.serial.models.Messages.{Unregister, Register, LineReceived}
import scala.collection.mutable

class LineReader(serialPort: SerialPort, respondTo: ActorRef) extends Actor {
  private val pipedOutputStr = new PipedOutputStream()
  private val bufferedInputStr = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutputStr)))
  private val subscribers = mutable.HashSet[ActorRef]()

  Stream.continually(bufferedInputStr.readLine()).foreach(line => respondTo ! LineReceived(line))

  override def receive: Receive = {
    case event: SerialPortEvent if event.isRXCHAR =>
      pipedOutputStr.write(serialPort.readBytes())
    case Register(actor) => subscribers += actor
    case Unregister(actor) => subscribers -= actor
  }
}
