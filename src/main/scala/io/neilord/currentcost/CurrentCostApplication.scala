package io.neilord.currentcost

import akka.actor._
import io.neilord.serial.SerialPortManager
import io.neilord.serial.factory.JSSCSerialPortFactory
import io.neilord.serial.models.Messages.OpenPort
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import io.neilord.serial.models.SerialPortConfig

object CurrentCostApplication extends App {

  val factory = new JSSCSerialPortFactory
  val baudRate = jssc.SerialPort.BAUDRATE_9600
  val dataBits = jssc.SerialPort.DATABITS_8
  val stopBits = jssc.SerialPort.STOPBITS_1
  val parity = jssc.SerialPort.PARITY_NONE

  val system = ActorSystem("SerialReaderSystem")

  val config: Config = ConfigFactory.load()

  val deviceName = config.as[String]("currentCostClient.serial.port")
  val host = config.as[String]("currentCostClient.kafkaBroker.host")
  val topic = config.as[String]("currentCostClient.kafkaBroker.topic")

  val portManager = system.actorOf(Props(classOf[SerialPortManager], factory), "serial-manager")
  val receiver = system.actorOf(Props(classOf[CurrentCostReadingReceiver], host, topic), "receiver")

  portManager.tell(OpenPort(SerialPortConfig(baudRate, dataBits, stopBits, parity, deviceName)), receiver)

  system.actorOf(Props(classOf[Terminator], portManager), "terminator")

  class Terminator(ref: ActorRef) extends Actor with ActorLogging {
    context watch ref
    def receive = {
      case Terminated(_) =>
        log.info("{} has terminated, shutting down system", ref.path)
        context.system.shutdown()
    }
  }
}
