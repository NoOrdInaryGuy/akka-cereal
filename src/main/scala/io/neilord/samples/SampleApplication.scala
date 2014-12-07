package io.neilord.samples

import akka.actor._
import akka.pattern.ask
import io.neilord.serial.{SerialPortSubscriptionManager, SerialPortManager}
import io.neilord.serial.factory.{TestSerialPortFactory, SerialPortFactory}
import io.neilord.serial.models.Messages.{CommandFailed, RegisterForBytes, PortOpened, OpenPort}
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import io.neilord.serial.models.SerialPortConfig
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import akka.util.Timeout
import scala.concurrent.duration._
import io.neilord.RealPropsProvider

object SampleApplication extends App {
  val serialPortFactory: SerialPortFactory = new TestSerialPortFactory

  val config: Config = ConfigFactory.load()
  val serialPortConfig = config.as[SerialPortConfig]("serial")

  val system = ActorSystem("AkkaCereal")
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val portManager = system.actorOf(
    Props(new SerialPortManager(serialPortFactory) with RealPropsProvider), "serial-manager")
  //val portManager = system.actorOf(Props(classOf[SerialPortManager], serialPortFactory), "serial-manager")
  val receiver = system.actorOf(Props(classOf[SampleSerialDataReceiver]), "receiver")

  val openPortFuture = portManager ? OpenPort(serialPortConfig)
  openPortFuture map {
    case PortOpened(subscriptionMgr) =>
      subscriptionMgr ! RegisterForBytes(receiver)
    case failed @ CommandFailed(cmd, throwable) =>
      println(s"Failed $cmd $throwable")
      system.shutdown()
  }

  system.actorOf(Props(classOf[Terminator], receiver), "terminator")

  class Terminator(ref: ActorRef) extends Actor with ActorLogging {
    context watch ref
    def receive = {
      case Terminated(_) =>
        log.info(s"${ref.path} has terminated, shutting down system")
        context.system.shutdown()
    }
  }
}
