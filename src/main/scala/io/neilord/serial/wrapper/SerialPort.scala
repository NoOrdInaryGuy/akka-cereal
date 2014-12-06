package io.neilord.serial.wrapper

import io.neilord.serial.models.SerialPortConfig
import scala.collection.immutable.Seq
import akka.actor.ActorRef

trait SerialPort {
  def open(): Boolean

  def close()

  def setParameters(serialPortParams: SerialPortConfig)

  def name: String

  def isOpen: Boolean

  def readBytes(): Seq[Byte]

  def subscribe(actorRef: ActorRef)

}
