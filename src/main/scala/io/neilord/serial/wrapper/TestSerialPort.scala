package io.neilord.serial.wrapper

import io.neilord.serial.models.SerialPortConfig
import scala.collection.immutable.Seq
import akka.actor.ActorRef
import io.neilord.serial.models.Messages.DataAvailable

class TestSerialPort(portName: String) extends SerialPort {

  class FakeEventProducer extends Runnable {
    override def run(): Unit = {
      while(true) {
        Thread.sleep(5000)
        subscriberOpt map {
          subscriber => {
            println("Data Ready")
            subscriber ! DataAvailable
          }
        }
      }
    }
  }

  new Thread(new FakeEventProducer).start()

  var subscriberOpt: Option[ActorRef] = None

  override def readBytes(): Seq[Byte] = {
    "Hello\nWorld\n".getBytes.toIndexedSeq
  }

  override def isOpen: Boolean = {
    true
  }

  override def name: String = {
    portName
  }

  override def setParameters(serialPortParams: SerialPortConfig): Unit = {

  }

  override def close(): Unit = {

  }

  override def open(): Boolean = {
    true
  }

  override def subscribe(actorRef: ActorRef) = {
    println(s"Set subscriber to $actorRef")
    subscriberOpt = Some(actorRef)
  }
}