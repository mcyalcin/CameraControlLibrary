package com.mikrotasarim.camera.device

class ConsoleMockDeviceInterface extends DeviceInterface {
  override def SetWireInValue(wireNumber: Int, value: Long) {
    println("Wire " + wireNumber + " set to value " + value.toBinaryString)
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
}
