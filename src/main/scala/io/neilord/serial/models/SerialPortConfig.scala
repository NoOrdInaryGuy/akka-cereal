package io.neilord.serial.models

case class SerialPortConfig(baudRate: Int, dataBits: Int, stopBits: Int, parity: Int, name: String)