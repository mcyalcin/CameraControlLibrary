package com.mikrotasarim.camera.device

class MockDeviceInterface(outputBuffer: StringBuilder) extends DeviceInterface {
  override def SetWireInValue(wireNumber: Int, value: Long) {
    outputBuffer.append("Wire " + wireNumber + " set to value " + value + "\n")
  }

  override def ActivateTriggerIn(address: Int, bit: Int) {
    outputBuffer.append("Trigger " + address + " set to value " + bit + "\n")
  }

  override def UpdateWireIns() {
    outputBuffer.append("Wire Ins Updated\n")
  }

  override def WriteToPipeIn(address: Int, size: Int, data: Array[Byte]) {
    outputBuffer.append("A data array of size " + data.length + " claimed to be of size " + size + " written to pipe " + address + "\n")
  }

  override def WriteToBlockPipeIn(address: Int, blockSize: Int, size: Int, data: Array[Byte]): Unit = ???

  override def Disconnect() {
    outputBuffer.append("Device disconnected.")
  }
}
