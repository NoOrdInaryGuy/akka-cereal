package unit

import io.neilord.serial.factory.SerialPortFactory
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import io.neilord.serial.models.SerialPortConfig
import io.neilord.serial.wrapper.SerialPort

trait SerialPortManagerContext extends MockitoSugar {
  val mockSerialPortFactory = mock[SerialPortFactory]

  val portName = "testName"
  val mockFactory = mock[SerialPortFactory]
  val mockSerialPort = mock[SerialPort]
  println(s"Mock is $mockSerialPort")
  val settings = SerialPortConfig(1, 2, 3, 4, portName)
  when(mockFactory.newInstance(portName)).thenReturn(mockSerialPort)
  when(mockSerialPort.open()).thenReturn(true)
  when(mockSerialPort.name).thenReturn(portName)

}
