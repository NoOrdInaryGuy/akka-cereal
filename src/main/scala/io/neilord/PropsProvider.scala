package io.neilord

import akka.actor.Props
import scala.reflect._

trait PropsProvider {
  def getProps(args: Any*): Props
}

trait RealPropsProvider extends PropsProvider {
  val ctag: ClassTag[_]

  override def getProps(args: Any*) = {
    Props(ctag.runtimeClass, args: _*)
  }
}