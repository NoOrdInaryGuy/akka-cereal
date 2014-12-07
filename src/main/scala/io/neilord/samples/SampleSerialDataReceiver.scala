package io.neilord.samples

import akka.actor.{ActorLogging, Actor}
import io.neilord.serial.models.Messages
import Messages._
import java.io.{PipedInputStream, InputStreamReader, BufferedReader, PipedOutputStream}

class SampleSerialDataReceiver extends Actor with ActorLogging {
  class LineReader extends Runnable {
    override def run() {
      Stream.continually(bufferedInputStr.readLine()).foreach(line => println(s"Read Line: $line"))
    }
  }

  private val pipedOutputStr = new PipedOutputStream()
  private val bufferedInputStr = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutputStr)))

  new Thread(new LineReader).start()

  override def receive: Receive = {
    case BytesReceived(content) => pipedOutputStr.write(content.toArray)
    case msg => log.warning(s"Unknown message received: $msg")
  }
}
