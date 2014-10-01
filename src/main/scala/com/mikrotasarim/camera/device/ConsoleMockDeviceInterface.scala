package com.mikrotasarim.camera.device

class ConsoleMockDeviceInterface extends DeviceInterface {
  override def SetWireInValue(wireNumber: Int, value: Long) {
    println("Wire " + wireNumber + " set to value " + value)
  }

  override def ActivateTriggerIn(address: Int, bit: Int) {
    println("Trigger " + address + " set to value " + bit)
  }

  override def UpdateWireIns() {
    println("Wire Ins Updated")
  }

  override def WriteToPipeIn(address: Int, size: Int, data: Array[Byte]) {
    println("A data array of size " + data.length + " claimed to be of size " + size + " written to pipe " + address)
  }

  override def WriteToBlockPipeIn(address: Int, blockSize: Int, size: Int, data: Array[Byte]): Unit = ???

  override def Disconnect() {
    println("Device disconnected.")
  }

  override def SetWireInValue(wireNumber: Int, value: Long, mask: Long): Unit =
    println("Wire " + wireNumber + " set to value " + value + " with mask " + mask)

  override def GetWireOutValue(address: Int): Long = {
    println(address + " read")
    0
  }

  override def UpdateWireOuts(): Unit = println("Wire Outs Updated")
}
