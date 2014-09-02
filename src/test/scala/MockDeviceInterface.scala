import com.mikrotasarim.camera.device.DeviceInterface

class MockDeviceInterface(outputBuffer: StringBuilder) extends DeviceInterface {
  override def SetWireInValue(wireNumber: Int, value: Long): Unit = outputBuffer.append("Wire " + wireNumber + " set to value " + value + "\n")

  override def ActivateTriggerIn(address: Int, bit: Int): Unit = outputBuffer.append("Trigger " + address + " set to value " + bit + "\n")

  override def UpdateWireIns(): Unit = outputBuffer.append("Wire Ins Updated\n")
}
