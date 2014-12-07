package unit

import io.neilord.serial.factory.SerialPortFactory
import org.scalatest.mock.MockitoSugar
import io.neilord.serial.models.SerialPortConfig
import io.neilord.serial.wrapper.SerialPort
import util.Messages.TestMessage

trait SerialPortManagerContext extends MockitoSugar {
  val portName = "testName"
  val settings = SerialPortConfig(1, 2, 3, 4, portName)
  val testMessage = TestMessage(System.currentTimeMillis())
  val mockSerialPortFactory = mock[SerialPortFactory]
  val mockFactory = mock[SerialPortFactory]
  val mockSerialPort = mock[SerialPort]
  val testException = mock[java.lang.RuntimeException]
}
