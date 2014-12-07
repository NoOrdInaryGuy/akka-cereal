package util

import akka.actor.{ActorRef, Props}
import io.neilord.PropsProvider

trait TestPropsProvider extends PropsProvider {
  val testProbeRef: ActorRef
  override def getProps(args: Any*) = Props(classOf[Wrapper], testProbeRef)
}
