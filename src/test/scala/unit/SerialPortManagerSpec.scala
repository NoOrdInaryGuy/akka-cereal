package unit

import org.scalatest.{Matchers, WordSpecLike}
import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit.{TestProbe, TestActorRef, ImplicitSender, TestKit}
import io.neilord.serial.models.Messages
import Messages.{CommandFailed, PortOpened, OpenPort}
import org.mockito.Mockito
import Mockito._
import io.neilord.serial.{SerialPortSubscriptionManager, SerialPortManager}
import scala.concurrent.duration._
import util.TestPropsProvider

class SerialPortManagerSpec extends TestKit(ActorSystem("test-manager-spec"))
  with WordSpecLike with Matchers with ImplicitSender {

  trait TestCase extends SerialPortManagerContext {
    val testProbe = new TestProbe(system)
    val serialPortManager = TestActorRef(
      Props(new SerialPortManager[SerialPortSubscriptionManager](mockFactory) with TestPropsProvider {
        val testProbeRef = testProbe.ref
      })
    )
  }

  "A SerialPortManager" should {
    "open and set the parameters on a SerialPort, and return a PortOpened msg" when {
      "receiving an OpenPort message" in new TestCase {
        when(mockFactory.newInstance(portName)).thenReturn(mockSerialPort)
        when(mockSerialPort.open()).thenReturn(true)
        when(mockSerialPort.name).thenReturn(portName)

        serialPortManager ! OpenPort(settings)

        expectMsgPF() {
          case PortOpened(handler: ActorRef) =>
            verify(mockSerialPort).open()
            verify(mockSerialPort).setParameters(settings)
            handler ! testMessage
            testProbe.expectMsg(1.second, testMessage)
        }
      }
    }

    "send a CommandFailed message" when {
      "an exception is thrown during the command" in new TestCase {
        when(mockFactory.newInstance(portName)).thenReturn(mockSerialPort)
        when(mockSerialPort.open()).thenThrow(testException)
        when(mockSerialPort.name).thenReturn(portName)

        val command = OpenPort(settings)
        serialPortManager ! OpenPort(settings)

        expectMsgPF() {
          case CommandFailed(`command`, `testException`) => true
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
