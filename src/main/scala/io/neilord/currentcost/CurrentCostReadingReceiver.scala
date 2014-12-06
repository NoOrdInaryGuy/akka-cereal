package io.neilord.currentcost

import akka.actor.{ActorLogging, Actor}
import io.neilord.serial.models.Messages
import Messages._
import Messages.RegisterForBytes
import Messages.PortOpened
import Messages.CommandFailed
import java.io.{PipedInputStream, InputStreamReader, BufferedReader, PipedOutputStream}
import akka.event.LoggingReceive
import java.nio.channels.Pipe
import java.nio.ByteBuffer

class CurrentCostReadingReceiver(brokerHost: String, brokerTopic: String) extends Actor with ActorLogging {
  class LineReader extends Runnable {
    override def run() {
      //TODO Stop using blocking IO here
      Stream.continually(bufferedInputStr.readLine()).foreach(line => println(s"Read Line: $line"))
      //TODO 1200 char limit for CurrentCost inputs
    }
  }

  private val pipedOutputStr = new PipedOutputStream()
  private val bufferedInputStr = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutputStr)))

  new Thread(new LineReader).start()

  override def receive: Receive = {
    case PortOpened(_, subscriptionMgr) =>
      log.info("ReadingReceiver notified that port is open, so subscribing for bytes")
      subscriptionMgr ! RegisterForBytes(context.self)
    case failed @ CommandFailed(cmd, throwable) => log.error(s"Failed $cmd $throwable")
    case BytesReceived(content) => {
      pipedOutputStr.write(content.toArray)
    }
    case msg => log.warning(s"Unknown message received: $msg")
  }
}
