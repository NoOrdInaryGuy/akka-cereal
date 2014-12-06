package io.neilord.currentcost

import akka.actor._
import io.neilord.serial.SerialPortManager
import io.neilord.serial.factory.{TestSerialPortFactory, SerialPortFactory, JSSCSerialPortFactory}
import io.neilord.serial.models.Messages.OpenPort
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import io.neilord.serial.models.SerialPortConfig

object CurrentCostApplication extends App {
  val config: Config = ConfigFactory.load()

  val deviceName = config.as[String]("serial.port")
  val baudRate = config.as[Int]("serial.baudRate")
  val dataBits = config.as[Int]("serial.dataBits")
  val stopBits = config.as[Int]("serial.stopBits")
  val parity = config.as[Int]("serial.parity")

  val host = config.as[String]("currentCostClient.kafkaBroker.host")
  val topic = config.as[String]("currentCostClient.kafkaBroker.topic")

  val serialPortFactory: SerialPortFactory = new TestSerialPortFactory

  val system = ActorSystem("SerialReaderSystem")

  val portManager = system.actorOf(Props(classOf[SerialPortManager], serialPortFactory), "serial-manager")
  val receiver = system.actorOf(Props(classOf[CurrentCostReadingReceiver], host, topic), "receiver")

  portManager.tell(OpenPort(SerialPortConfig(baudRate, dataBits, stopBits, parity, deviceName)), receiver)

  system.actorOf(Props(classOf[Terminator], portManager), "terminator")

  class Terminator(ref: ActorRef) extends Actor with ActorLogging {
    context watch ref
    def receive = {
      case Terminated(_) =>
        log.info(s"${ref.path} has terminated, shutting down system")
        context.system.shutdown()
    }
  }
}
