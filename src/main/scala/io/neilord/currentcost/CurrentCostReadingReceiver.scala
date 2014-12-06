package io.neilord.currentcost

import akka.actor.{ActorLogging, Actor}
import io.neilord.serial.models.Messages
import Messages._
import Messages.Register
import Messages.PortOpened
import Messages.CommandFailed

class CurrentCostReadingReceiver(brokerHost: String, brokerTopic: String) extends Actor with ActorLogging {

  override def receive: Receive = {
    case PortOpened(_, subscriptionMgr) => subscriptionMgr ! Register(context.self)
    case failed @ CommandFailed(cmd, throwable) => log.error(s"Failed $cmd $throwable")
    case LineReceived(line) => {
      if(line.length > 1200) {
        log.info(s"Reading Taken: $line")
      }
    }
    case _ => log.info("Unknown")
  }
}
