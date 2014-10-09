package com.mikrotasarim.ui.model

import DeviceInterfaceModel._
import scalafx.beans.property.StringProperty

object MtAs1410x2MemoryMap {

  def ReadAsicMemory() {
    for (i <- 0 to 127) ReadAsicMemory(i)
  }

  def ReadAsicMemory(address: Int) {
    commandFactory.MakeReadFromAsicMemoryTopCommand(address, SetAddress(address, _)).Execute()
  }

  def SetAddress(address: Int, value: String): Unit = SetAddress(address, Integer.parseInt(value, 2))
  def SetAddress(address: Int, value: Long): Unit = {
    memory(address) = value
    memoryModel(address).value = String.format("%16s",value.toBinaryString).replace(' ', '0')
  }

  val memory = new Array[Long](128)
  val memoryModel = new Array[StringProperty](128)
  for (i <- 0 to 127) memoryModel(i) = new StringProperty()

  object TimingGeneratorMemorySegment
  object AdcChannelMemorySegment
  object OutputStageMemorySegment
  object BiasGeneratorMemorySegment
  object DigitalControllerMemorySegment
}
