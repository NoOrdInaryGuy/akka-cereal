package unit

import org.scalatest.{Matchers, WordSpecLike}
import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import io.neilord.serial.models.Messages
import Messages.{CommandFailed, PortOpened, OpenPort}
import org.mockito.Mockito
import Mockito._
import io.neilord.serial.SerialPortManager

class SerialPortManagerSpec extends TestKit(ActorSystem("test-registry-spec"))
  with WordSpecLike with Matchers with ImplicitSender {

  "A SerialPortManager" should {
    "open and set the parameters on a SerialPort, and return a PortOpened msg" when {
      "receiving an OpenPort message" in new SerialPortManagerContext {
        val serialPortManager = TestActorRef(Props(classOf[SerialPortManager], mockFactory))
        serialPortManager ! OpenPort(settings)

        Thread.sleep(1000L)

        verify(mockSerialPort).open()
        verify(mockSerialPort).setParameters(settings)

        expectMsgPF() {
          case PortOpened(`settings`, _) => true
        }
      }
    }

    "send a CommandFailed message" when {
      "an exception is thrown during the command" in new SerialPortManagerExceptionContext {
        val serialPortManager = TestActorRef(Props(classOf[SerialPortManager], mockFactory))
        val msg = OpenPort(settings)
        serialPortManager ! msg

        expectMsgPF() {
          case CommandFailed(`msg`, `testException`) => true
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
