package util

import akka.actor.{Actor, ActorRef}

class Wrapper(target: ActorRef) extends Actor {
  def receive = {
    case msg => target forward msg
  }
}
