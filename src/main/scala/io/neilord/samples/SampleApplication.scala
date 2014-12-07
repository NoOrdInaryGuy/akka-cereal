package io.neilord.samples

import akka.actor._
import akka.pattern.ask
import io.neilord.serial.SerialPortManager
import io.neilord.serial.factory.{TestSerialPortFactory, SerialPortFactory}
import io.neilord.serial.models.Messages.{CommandFailed, RegisterForBytes, PortOpened, OpenPort}
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import io.neilord.serial.models.SerialPortConfig
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import akka.util.Timeout
import scala.concurrent.duration._

object SampleApplication extends App {
  //Factories (could use DI here)
  val serialPortFactory: SerialPortFactory = new TestSerialPortFactory

  //Config
  val config: Config = ConfigFactory.load()
  val serialPortConfig = config.as[SerialPortConfig]("serial")

  //Actor system, and some implicit settings
  val system = ActorSystem("AkkaCereal")
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  //Bootstrap the app:
  //1. Get a port manager
  val portManager = system.actorOf(Props(SerialPortManager(serialPortFactory)), "serial-manager")
  //2. This is the application that will use serial data
  val receiver = system.actorOf(Props(classOf[SampleSerialDataReceiver]), "receiver")

  //3. Ask the port manage to open the port, and subscribe the receiver to it using the returned
  // subscription manager.
  val openPortFuture = portManager ? OpenPort(serialPortConfig)
  openPortFuture map {
    case PortOpened(subscriptionMgr) =>
      subscriptionMgr ! RegisterForBytes(receiver)
    case failed @ CommandFailed(cmd, throwable) =>
      println(s"Failed $cmd $throwable")
      system.shutdown()
  }

}
