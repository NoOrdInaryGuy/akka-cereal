package unit

import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import io.neilord.serial.factory.SerialPortFactory
import io.neilord.serial.models.SerialPortConfig
import io.neilord.serial.wrapper.SerialPort

trait SerialPortManagerExceptionContext extends MockitoSugar {
  val mockSerialPortFactory = mock[SerialPortFactory]

  val portName = "testName2"
  val mockFactory = mock[SerialPortFactory]
  val mockSerialPort = mock[SerialPort]
  val settings = SerialPortConfig(1, 2, 3, 4, portName)

  val testException = mock[java.lang.RuntimeException]
  when(mockSerialPort.open()).thenThrow(testException)
  when(mockFactory.newInstance(portName)).thenReturn(mockSerialPort)
  when(mockSerialPort.name).thenReturn(portName)

}
