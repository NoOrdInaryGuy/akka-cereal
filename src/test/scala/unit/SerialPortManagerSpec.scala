package unit

import org.scalatest.{Matchers, WordSpecLike}
import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import io.neilord.serial.models.Messages
import Messages.{CommandFailed, PortOpened, OpenPort}
import org.mockito.Mockito
import Mockito._
import io.neilord.serial.{SerialPortSubscriptionManager, SerialPortManager}
import io.neilord.RealPropsProvider

class SerialPortManagerSpec extends TestKit(ActorSystem("test-registry-spec"))
  with WordSpecLike with Matchers with ImplicitSender {

  "A SerialPortManager" should {
    "open and set the parameters on a SerialPort, and return a PortOpened msg" when {
      "receiving an OpenPort message" in new SerialPortManagerContext {
        //TODO make TestPropsProvider then make sure it's the right one
        val serialPortManager = TestActorRef(
          Props(new SerialPortManager[SerialPortSubscriptionManager](mockFactory) with RealPropsProvider)
        )
        serialPortManager ! OpenPort(settings)

        Thread.sleep(1000L)

        //TODO Make probe send a message to verify it pops out of the hander

        verify(mockSerialPort).open()
        verify(mockSerialPort).setParameters(settings)

        expectMsgPF() {
          case PortOpened(handler: ActorRef) => {
            handler
          }
        }
      }
    }

    "send a CommandFailed message" when {
      "an exception is thrown during the command" in new SerialPortManagerExceptionContext {
        val serialPortManager = TestActorRef(
          Props(new SerialPortManager[SerialPortSubscriptionManager](mockFactory) with RealPropsProvider)
        )
        val msg = OpenPort(settings)
        serialPortManager ! msg

        expectMsgPF() {
          //case CommandFailed(`msg`, `testException`) => true
          case msg: CommandFailed => println(msg)
        }
      }
    }

    "correctly escape port names" in {
      SerialPortManager.escapePort("/slash/slash/") should equal("-slash-slash-")
      SerialPortManager.escapePort("//slash//slash//") should equal("--slash--slash--")
      SerialPortManager.escapePort("none") should equal("none")
      SerialPortManager.escapePort("-/none") should equal("--none")
    }
  }

}
